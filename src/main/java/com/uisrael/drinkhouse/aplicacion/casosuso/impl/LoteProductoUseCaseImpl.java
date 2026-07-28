package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.NoSuchElementException;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;

public class LoteProductoUseCaseImpl implements ILoteProductoUseCase {

	private final ILoteProductoRepositorio repositorio;

	public LoteProductoUseCaseImpl(ILoteProductoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public LoteProducto crear(LoteProducto loteProducto) {
		loteProducto.setLoteId(null);
		return repositorio.guardar(loteProducto);
	}

	@Override
	public LoteProducto actualizar(Long id, LoteProducto loteProducto) {
		buscarPorId(id);
		loteProducto.setLoteId(id);
		return repositorio.guardar(loteProducto);
	}

	@Override
	public LoteProducto buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NoSuchElementException("Lote de producto no encontrado: " + id));
	}

	@Override
	public List<LoteProducto> listar(Long productoId) {
		return productoId != null ? repositorio.listarPorProducto(productoId) : repositorio.listarTodos();
	}

	@Override
	public List<LoteProducto> listarProximosAVencer(int dias) {
		return repositorio.listarProximosAVencer(dias);
	}

	@Override
	public LoteProducto actualizarCantidad(Long id, Integer cantidadDisponible) {
		return repositorio.actualizarCantidad(id, cantidadDisponible);
	}

	@Override
	public LoteProducto activar(Long id) {
		return repositorio.activar(id);
	}

	@Override
	public LoteProducto desactivar(Long id) {
		return repositorio.desactivar(id);
	}

	@Override
	public void eliminar(Long id) {
		repositorio.eliminar(id);
	}
}
