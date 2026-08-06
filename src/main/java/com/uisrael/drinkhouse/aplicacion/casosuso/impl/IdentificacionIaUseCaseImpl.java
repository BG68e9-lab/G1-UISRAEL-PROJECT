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
import com.uisrael.drinkhouse.presentacion.dto.response.RespuestaClaudeDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoProductoDto;

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

		if (formatoImagen == null || !esFormatoSoportado(formatoImagen)) {
			throw new ReglaNegocioException(
					"Formato de imagen no soportado. Use JPEG, PNG o WEBP");
		}

		if (tipoIdentificacion == null ||
				(!"BOTELLA".equalsIgnoreCase(tipoIdentificacion) && 
				 !"FACTURA".equalsIgnoreCase(tipoIdentificacion) &&
				 !"PRODUCTO".equalsIgnoreCase(tipoIdentificacion))) {
			throw new ReglaNegocioException(
					"Tipo de identificación no válido. Use PRODUCTO, BOTELLA o FACTURA");
		}

		if (productoId != null) {
			productoRepositorio.buscarPorId(productoId)
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"Producto no encontrado con id: " + productoId));
		}

		TokensIaNegocio limiteTokens = tokensRepositorio.buscarPorNegocioId(negocioId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Configuración de tokens IA no encontrada para el negocio: " + negocioId));

		if (Boolean.FALSE.equals(limiteTokens.getActivo())) {
			throw new CuotaIaExcedidaException("El token de IA del negocio no está activo");
		}

		LocalDate periodoActual = LocalDate.now().withDayOfMonth(1);
		ConsumoIaMensual consumo = consumoRepositorio
				.buscarPorNegocioYPeriodo(negocioId, periodoActual)
				.orElse(ConsumoIaMensual.builder()
						.negocioId(negocioId)
						.periodo(periodoActual)
						.totalTokensInput(0L)
						.totalTokensOutput(0L)
						.build());

		RespuestaClaudeConIdentificacion resultado = procesarConClaude(
				imagenBase64, formatoImagen, tipoIdentificacion, productoId, negocioId);

		IdentificacionIa guardada = identificacionRepositorio.guardar(resultado.getIdentificacion());

		consumo.setTotalTokensInput(consumo.getTotalTokensInput() + resultado.getTokensInput());
		consumo.setTotalTokensOutput(consumo.getTotalTokensOutput() + resultado.getTokensOutput());
		consumoRepositorio.guardar(consumo);

		return guardada;
	}

	@Override
	public List<IdentificacionIa> consultarHistorial(Long productoId,
			OffsetDateTime desde, OffsetDateTime hasta) {
		return identificacionRepositorio.buscarConFiltros(productoId, desde, hasta);
	}

	/**
	 * Clase interna para envolver la identificación con sus tokens consumidos.
	 */
	private static class RespuestaClaudeConIdentificacion {
		private final IdentificacionIa identificacion;
		private final Long tokensInput;
		private final Long tokensOutput;

		public RespuestaClaudeConIdentificacion(IdentificacionIa identificacion, Long tokensInput, Long tokensOutput) {
			this.identificacion = identificacion;
			this.tokensInput = tokensInput;
			this.tokensOutput = tokensOutput;
		}

		public IdentificacionIa getIdentificacion() {
			return identificacion;
		}

		public Long getTokensInput() {
			return tokensInput;
		}

		public Long getTokensOutput() {
			return tokensOutput;
		}
	}

	/**
	 * Delega el análisis a Claude y construye la entidad de dominio con el resultado.
	 *
	 * @param imagenBase64       imagen en base64
	 * @param formatoImagen      formato de la imagen
	 * @param tipoIdentificacion PRODUCTO, BOTELLA o FACTURA
	 * @param productoId         ID del producto
	 * @param negocioId          ID del negocio
	 * @return entidad de dominio con tokens consumidos
	 */
	private RespuestaClaudeConIdentificacion procesarConClaude(String imagenBase64, String formatoImagen,
			String tipoIdentificacion, Long productoId, Integer negocioId) {

		IdentificacionIa.IdentificacionIaBuilder constructor = IdentificacionIa.builder()
				.negocioId(negocioId)
				.productoId(productoId)
				.archivoUrl(imagenBase64 != null ? "base64://" + imagenBase64.length() + "bytes" : null)
				.modeloIaUsado(NOMBRE_MODELO)
				.creadoEn(OffsetDateTime.now());

		Long tokensInput = 0L;
		Long tokensOutput = 0L;

		if ("PRODUCTO".equalsIgnoreCase(tipoIdentificacion)) {
			RespuestaClaudeDto<ResultadoProductoDto> respuesta = claudeVisionService.identificarProductoGenerico(imagenBase64, formatoImagen);
			ResultadoProductoDto resultado = respuesta.getResultado();
			tokensInput = respuesta.getTokensInput();
			tokensOutput = respuesta.getTokensOutput();
			constructor
					.nombreSugerido(resultado.getNombre())
					.marcaSugerida(resultado.getMarca())
					.tipoSugerido(resultado.getCategoriaSugerida())
					.reconocido(resultado.getReconocido() != null ? resultado.getReconocido() : false);
		} else if ("BOTELLA".equalsIgnoreCase(tipoIdentificacion)) {
			RespuestaClaudeDto<ResultadoBotellaDto> respuesta = claudeVisionService.identificarBotella(imagenBase64, formatoImagen);
			ResultadoBotellaDto resultado = respuesta.getResultado();
			tokensInput = respuesta.getTokensInput();
			tokensOutput = respuesta.getTokensOutput();
			constructor
					.nombreSugerido(resultado.getNombre())
					.marcaSugerida(resultado.getMarca())
					.tipoSugerido(resultado.getTipo())
					.reconocido(resultado.getReconocido() != null ? resultado.getReconocido() : false);
		} else {
			RespuestaClaudeDto<ResultadoFacturaDto> respuesta = claudeVisionService.extraerFactura(imagenBase64, formatoImagen);
			ResultadoFacturaDto resultado = respuesta.getResultado();
			tokensInput = respuesta.getTokensInput();
			tokensOutput = respuesta.getTokensOutput();
			constructor
					.nombreSugerido(resultado.getRazonSocialProveedor())
					.marcaSugerida(null)
					.tipoSugerido("FACTURA")
					.reconocido(resultado.getNumeroFactura() != null);
		}

		IdentificacionIa identificacion = constructor.build();
		return new RespuestaClaudeConIdentificacion(identificacion, tokensInput, tokensOutput);
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
