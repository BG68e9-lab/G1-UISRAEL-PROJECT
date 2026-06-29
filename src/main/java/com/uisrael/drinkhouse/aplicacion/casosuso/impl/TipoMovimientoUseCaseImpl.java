package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

public class TipoMovimientoUseCaseImpl implements ITipoMovimientoUseCase {

	private final ITipoMovimientoRepositorio repositorio;

	public TipoMovimientoUseCaseImpl(ITipoMovimientoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public TipoMovimiento guardar(TipoMovimiento tipomovimiento) {

		return repositorio.guardar(tipomovimiento);
	}

	@Override
	public TipoMovimiento buscarPorId(int id) {

		return repositorio.buscarPorId(id).orElseThrow(() -> new RuntimeException("Tipo de Movimiento no encontrado"));
	}

	@Override
	public List<TipoMovimiento> listarTodos() {

		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {

		repositorio.eliminar(id);

	}

}
