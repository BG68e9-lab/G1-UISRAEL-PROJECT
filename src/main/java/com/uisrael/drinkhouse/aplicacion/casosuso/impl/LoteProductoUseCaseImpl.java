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

@Override
	public LoteProducto crearLote(LoteProducto lote, Long productoId) {
		if (lote.getCantidadInicial() == null
				|| lote.getCantidadInicial().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaNegocioException(
					"La cantidadInicial debe ser mayor a cero");
		}

		productoRepositorio.buscarPorId(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + productoId));

		lote.setCantidadDisponible(lote.getCantidadInicial());

		lote.setFechaIngreso(OffsetDateTime.now());

		TipoMovimiento tipoLote = tipoMovimientoRepositorio.buscarPorCodigo("LOTE")
				.orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento LOTE no configurado"));
		Long secuencia = secuenciaCodigoUseCase.siguiente(lote.getNegocioId(), tipoLote.getTipoMovimientoId());
		String codigoEntrada = "LOTE-" + String.format("%08d", secuencia);
		lote.setCodigoEntrada(codigoEntrada);

		return repositorio.guardarConProductoId(lote, productoId);
	}

@Override
	public List<LoteProducto> buscarPorProducto(Long productoId) {
		return repositorio.buscarPorProductoOrdenadoPorFechaIngreso(productoId);
	}

@Override
	public LoteProducto buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Lote de producto no encontrado con id: " + id));
	}

@Override
	public List<LoteProducto> buscarProximosAVencer(int dias) {
		LocalDate limite = LocalDate.now().plusDays(dias);
		return repositorio.buscarProximosAVencer(limite);
	}

@Override
	public Page<LoteProducto> listarPaginado(Pageable pageable) {
		return repositorio.listarPaginado(pageable);
	}
}
