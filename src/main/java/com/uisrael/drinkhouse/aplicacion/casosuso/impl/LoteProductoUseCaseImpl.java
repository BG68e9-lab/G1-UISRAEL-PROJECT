package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

/**
 * Implementación del caso de uso de Lotes de Producto.
 * Gestiona la creación y consulta de lotes con generación de códigos únicos.
 */
public class LoteProductoUseCaseImpl implements ILoteProductoUseCase {

	private final ILoteProductoRepositorio repositorio;
	private final IProductoRepositorio productoRepositorio;
	private final ISecuenciaCodigoUseCase secuenciaCodigoUseCase;
	private final ITipoMovimientoRepositorio tipoMovimientoRepositorio;

	public LoteProductoUseCaseImpl(
			ILoteProductoRepositorio repositorio,
			IProductoRepositorio productoRepositorio,
			ISecuenciaCodigoUseCase secuenciaCodigoUseCase,
			ITipoMovimientoRepositorio tipoMovimientoRepositorio) {
		this.repositorio = repositorio;
		this.productoRepositorio = productoRepositorio;
		this.secuenciaCodigoUseCase = secuenciaCodigoUseCase;
		this.tipoMovimientoRepositorio = tipoMovimientoRepositorio;
	}

	/**
	 * Crea un nuevo lote de producto.
	 * Reglas de negocio:
	 * - cantidadInicial debe ser mayor a 0; si no, lanza ReglaNegocioException (400)
	 * - productoId debe existir; si no, lanza RecursoNoEncontradoException (404)
	 * - Asigna cantidadDisponible = cantidadInicial
	 * - Asigna fechaIngreso = ahora
	 * - Genera codigoEntrada = "LOTE-" + número secuencial de 8 dígitos
	 */
	@Override
	@Transactional
	public LoteProducto crearLote(LoteProducto lote, Long productoId) {
		// Validar cantidadInicial > 0 (Requisito 5.5)
		if (lote.getCantidadInicial() == null
				|| lote.getCantidadInicial().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaNegocioException(
					"La cantidadInicial debe ser mayor a cero");
		}

		// Verificar que el producto existe (Requisito 5.6)
		productoRepositorio.buscarPorId(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + productoId));

		// Asignar cantidadDisponible = cantidadInicial (Requisito 5.1)
		lote.setCantidadDisponible(lote.getCantidadInicial());

		// Registrar fechaIngreso con la fecha-hora actual (Requisito 5.1)
		lote.setFechaIngreso(OffsetDateTime.now());

		// Generar código de entrada único (Requisito 5.1, 15.4)
		TipoMovimiento tipoLote = tipoMovimientoRepositorio.buscarPorCodigo("LOTE")
				.orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento LOTE no configurado"));
		Long secuencia = secuenciaCodigoUseCase.siguiente(lote.getNegocioId(), tipoLote.getTipoMovimientoId());
		String codigoEntrada = "LOTE-" + String.format("%08d", secuencia);
		lote.setCodigoEntrada(codigoEntrada);

		// Persistir el lote asociado al producto
		return repositorio.guardarConProductoId(lote, productoId);
	}

	/**
	 * Retorna todos los lotes de un producto ordenados por fechaIngreso ASC (FIFO).
	 * (Requisito 5.2)
	 */
	@Override
	public List<LoteProducto> buscarPorProducto(Long productoId) {
		return repositorio.buscarPorProductoOrdenadoPorFechaIngreso(productoId);
	}

	/**
	 * Busca un lote por su identificador.
	 * (Requisito 5.3)
	 */
	@Override
	public LoteProducto buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Lote de producto no encontrado con id: " + id));
	}

	/**
	 * Retorna los lotes cuya fechaVencimiento <= hoy+N días y cantidadDisponible > 0.
	 * (Requisito 5.4)
	 *
	 * @param dias número de días desde hoy
	 */
	@Override
	public List<LoteProducto> buscarProximosAVencer(int dias) {
		LocalDate limite = LocalDate.now().plusDays(dias);
		return repositorio.buscarProximosAVencer(limite);
	}

	/**
	 * Lista todos los lotes con paginación.
	 */
	@Override
	public Page<LoteProducto> listarPaginado(Pageable pageable) {
		return repositorio.listarPaginado(pageable);
	}
}
