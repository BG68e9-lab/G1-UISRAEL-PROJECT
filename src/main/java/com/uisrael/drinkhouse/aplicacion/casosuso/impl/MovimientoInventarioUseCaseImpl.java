package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;

public class MovimientoInventarioUseCaseImpl implements IMovimientoInventarioUseCase {

	private final IMovimientoInventarioRepositorio repositorio;

	public MovimientoInventarioUseCaseImpl(IMovimientoInventarioRepositorio repositorio) {

		this.repositorio = repositorio;
	}

	@Override
	public MovimientoInventario guardar(MovimientoInventario movimientoinventario) {

		return repositorio.guardar(movimientoinventario);
	}

	@Override
	public MovimientoInventario buscarPorId(int id) {

		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RuntimeException("Movimiento por Inventario no encontrado"));
	}

	@Override
	public List<MovimientoInventario> listarTodo() {

		return repositorio.listarTodo();
	}

	@Override
	public void eliminar(int id) {

		repositorio.eliminar(id);

	}

}
