package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IIdentificacionIaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.ConsumoIaMensual;
import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;
import com.uisrael.drinkhouse.aplicacion.excepciones.CuotaIaExcedidaException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.IConsumoIaMensualRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IIdentificacionIaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITokensIaNegocioRepositorio;
import com.uisrael.drinkhouse.infraestructura.servicios.ClaudeVisionService;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;

/**
 * Implementación del caso de uso de identificación mediante IA.
 * Delega el análisis de imágenes a Claude Vision (Anthropic) y
 * registra el consumo de tokens por negocio/mes.
 */
public class IdentificacionIaUseCaseImpl implements IIdentificacionIaUseCase {

	private static final String NOMBRE_MODELO = "claude-3-5-sonnet-20241022";

	private final IIdentificacionIaRepositorio identificacionRepositorio;
	private final IConsumoIaMensualRepositorio consumoRepositorio;
	private final ITokensIaNegocioRepositorio tokensRepositorio;
	private final IProductoRepositorio productoRepositorio;
	private final ClaudeVisionService claudeVisionService;

	public IdentificacionIaUseCaseImpl(
			IIdentificacionIaRepositorio identificacionRepositorio,
			IConsumoIaMensualRepositorio consumoRepositorio,
			ITokensIaNegocioRepositorio tokensRepositorio,
			IProductoRepositorio productoRepositorio,
			ClaudeVisionService claudeVisionService) {
		this.identificacionRepositorio = identificacionRepositorio;
		this.consumoRepositorio = consumoRepositorio;
		this.tokensRepositorio = tokensRepositorio;
		this.productoRepositorio = productoRepositorio;
		this.claudeVisionService = claudeVisionService;
	}

	@Override
	@Transactional
	public IdentificacionIa identificarProducto(String imagenBase64, String formatoImagen,
			Long productoId, Integer negocioId, String tipoIdentificacion) {

		// 1. Validar formato de imagen
		if (formatoImagen == null || !esFormatoSoportado(formatoImagen)) {
			throw new ReglaNegocioException(
					"Formato de imagen no soportado. Use JPEG, PNG o WEBP");
		}

		// 2. Validar tipo de identificación
		if (tipoIdentificacion == null ||
				(!"BOTELLA".equalsIgnoreCase(tipoIdentificacion) && !"FACTURA".equalsIgnoreCase(tipoIdentificacion))) {
			throw new ReglaNegocioException(
					"Tipo de identificación no válido. Use BOTELLA o FACTURA");
		}

		// 3. Verificar existencia del producto
		productoRepositorio.buscarPorId(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + productoId));

		// 4. Obtener configuración de tokens del negocio
		TokensIaNegocio limiteTokens = tokensRepositorio.buscarPorNegocioId(negocioId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Configuración de tokens IA no encontrada para el negocio: " + negocioId));

		// Solo verificar si hay token activo
		if (Boolean.FALSE.equals(limiteTokens.getActivo())) {
			throw new CuotaIaExcedidaException("El token de IA del negocio no está activo");
		}

		// 5. Obtener o crear registro de consumo del mes actual (periodo = 1er día del mes)
		LocalDate periodoActual = LocalDate.now().withDayOfMonth(1);
		ConsumoIaMensual consumo = consumoRepositorio
				.buscarPorNegocioYPeriodo(negocioId, periodoActual)
				.orElse(ConsumoIaMensual.builder()
						.negocioId(negocioId)
						.periodo(periodoActual)
						.totalTokensInput(0L)
						.totalTokensOutput(0L)
						.build());

		// 6. Llamar a Claude según el tipo de identificación y mapear el resultado
		IdentificacionIa identificacion = procesarConClaude(
				imagenBase64, formatoImagen, tipoIdentificacion, productoId, negocioId);

		// 7. Guardar la identificación
		IdentificacionIa guardada = identificacionRepositorio.guardar(identificacion);

		// 8. Incrementar tokens usados y guardar consumo
		consumo.setTotalTokensInput(consumo.getTotalTokensInput() + 1L);
		consumo.setTotalTokensOutput(consumo.getTotalTokensOutput() + 1L);
		consumoRepositorio.guardar(consumo);

		return guardada;
	}

	@Override
	public List<IdentificacionIa> consultarHistorial(Long productoId,
			OffsetDateTime desde, OffsetDateTime hasta) {
		return identificacionRepositorio.buscarConFiltros(productoId, desde, hasta);
	}

	/**
	 * Delega el análisis a Claude y construye la entidad de dominio con el resultado.
	 *
	 * @param imagenBase64       imagen en base64
	 * @param formatoImagen      formato de la imagen
	 * @param tipoIdentificacion BOTELLA o FACTURA
	 * @param productoId         ID del producto
	 * @param negocioId          ID del negocio
	 * @return entidad de dominio lista para persistir
	 */
	private IdentificacionIa procesarConClaude(String imagenBase64, String formatoImagen,
			String tipoIdentificacion, Long productoId, Integer negocioId) {

		IdentificacionIa.IdentificacionIaBuilder constructor = IdentificacionIa.builder()
				.negocioId(negocioId)
				.productoId(productoId)
				.archivoUrl(imagenBase64 != null ? "base64://" + imagenBase64.length() + "bytes" : null)
				.modeloIaUsado(NOMBRE_MODELO)
				.creadoEn(OffsetDateTime.now());

		if ("BOTELLA".equalsIgnoreCase(tipoIdentificacion)) {
			ResultadoBotellaDto resultado = claudeVisionService.identificarBotella(imagenBase64, formatoImagen);
			constructor
					.nombreSugerido(resultado.getNombre())
					.marcaSugerida(resultado.getMarca())
					.tipoSugerido(resultado.getTipo())
					.reconocido(resultado.getReconocido() != null ? resultado.getReconocido() : false);
		} else {
			// FACTURA: se usa el nombre del proveedor como nombre sugerido
			ResultadoFacturaDto resultado = claudeVisionService.extraerFactura(imagenBase64, formatoImagen);
			constructor
					.nombreSugerido(resultado.getRazonSocialProveedor())
					.marcaSugerida(null)
					.tipoSugerido("FACTURA")
					.reconocido(resultado.getNumeroFactura() != null);
		}

		return constructor.build();
	}

	/**
	 * Verifica si el formato de imagen es soportado por la API de Claude.
	 *
	 * @param formato nombre del formato (JPEG, PNG, WEBP)
	 * @return true si el formato es válido
	 */
	private boolean esFormatoSoportado(String formato) {
		String f = formato.toUpperCase();
		return "JPEG".equals(f) || "PNG".equals(f) || "WEBP".equals(f);
	}
}
