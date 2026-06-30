package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Negocio;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;

public class NegocioUseCaseImpl implements INegocioUseCase {
	
	private final INegocioRepositorio repositorio;	

	public NegocioUseCaseImpl(INegocioRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Negocio guardar(Negocio nuevoNegocio) {
		return repositorio.guardar(nuevoNegocio);
	}

	@Override
	public Negocio buscarporId(Integer idNegocio) {
		return repositorio.buscarporId(idNegocio)
				.orElseThrow(() -> new RuntimeException("Negocio no encontrado"));
	}

	@Override
	public List<Negocio> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(Integer idNegocio) {
		repositorio.eliminar(idNegocio);
	}

}
