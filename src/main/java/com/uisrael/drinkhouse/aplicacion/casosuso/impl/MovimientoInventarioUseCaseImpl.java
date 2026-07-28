package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.NoSuchElementException;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;

public class MovimientoInventarioUseCaseImpl implements IMovimientoInventarioUseCase {

	private final IMovimientoInventarioRepositorio repositorio;

	public MovimientoInventarioUseCaseImpl(IMovimientoInventarioRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public MovimientoInventario guardar(MovimientoInventario movimientoInventario) {
		return repositorio.guardar(movimientoInventario);
	}

	@Override
	public MovimientoInventario buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NoSuchElementException("Movimiento de inventario no encontrado: " + id));
	}

	@Override
	public List<MovimientoInventario> listarTodo(String tipo) {
		return repositorio.listarTodo(tipo);
	}

	@Override
	public void eliminar(Long id) {
		repositorio.eliminar(id);
	}
}
