package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoOcUseCase;
import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoOcRepositorio;

public class EstadoOcUseCaseImpl implements IEstadoOcUseCase {
	
	private final IEstadoOcRepositorio repositorio;

	public EstadoOcUseCaseImpl(IEstadoOcRepositorio repositorio) {
		super();
		this.repositorio = repositorio;
	}

	@Override
	public EstadoOc guardar(EstadoOc nuevoEstadoOc) {
		return repositorio.guardar(nuevoEstadoOc);
	}

	@Override
	public EstadoOc buscarPorId(Integer idEstadoOc) {
		return repositorio.buscarPorId(idEstadoOc)
				.orElseThrow(() -> new RuntimeException("Estado oc no encontrado"));
	}

	@Override
	public List<EstadoOc> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(Integer idEstadoOc) {
		repositorio.eliminar(idEstadoOc);
	}

}
