package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

public class TipoMovimientoUseCaseImpl implements ITipoMovimientoUseCase {

	private final ITipoMovimientoRepositorio repositorio;

	public TipoMovimientoUseCaseImpl(ITipoMovimientoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public TipoMovimiento crearTipoMovimiento(TipoMovimiento tipoMovimiento) {
		if (repositorio.existePorCodigo(tipoMovimiento.getCodigo())) {
			throw new ConflictoUnicoException("Ya existe un tipo de movimiento con código: "
					+ tipoMovimiento.getCodigo());
		}
		return repositorio.guardar(tipoMovimiento);
	}

	@Override
	public TipoMovimiento buscarPorId(Integer id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de movimiento no encontrado con id: " + id));
	}

	@Override
	public List<TipoMovimiento> listarTodos() {
		return repositorio.listarTodos();
	}
}
