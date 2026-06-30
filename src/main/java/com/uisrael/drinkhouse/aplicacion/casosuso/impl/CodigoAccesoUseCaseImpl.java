package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;

public class CodigoAccesoUseCaseImpl implements ICodigoAccesoUseCase {
	
	private final ICodigoAccesoRepositorio repositorio;

	public CodigoAccesoUseCaseImpl(ICodigoAccesoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public CodigoAcceso guardar(CodigoAcceso nuevoCodigoAcceso) {
		return repositorio.guardar(nuevoCodigoAcceso);
	}

	@Override
	public CodigoAcceso buscarPorId(UUID idCodigoAcceso) {
		return repositorio.buscarPorId(idCodigoAcceso)
				.orElseThrow(() -> new RuntimeException("Codigo acceso no encontrado"));
	}

	@Override
	public List<CodigoAcceso> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(UUID idCodigoAcceso) {
		repositorio.eliminar(idCodigoAcceso);
	}

}
