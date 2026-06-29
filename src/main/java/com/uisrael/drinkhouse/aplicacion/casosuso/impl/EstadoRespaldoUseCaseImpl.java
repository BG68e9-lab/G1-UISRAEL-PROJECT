package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoRespaldoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoRespaldoRepositorio;

public class EstadoRespaldoUseCaseImpl implements IEstadoRespaldoUseCase {

	private final IEstadoRespaldoRepositorio repositorio;

	public EstadoRespaldoUseCaseImpl(IEstadoRespaldoRepositorio repositorio) {

		this.repositorio = repositorio;
	}

	@Override
	public EstadoRespaldo guardar(EstadoRespaldo estadorespaldo) {

		return repositorio.guardar(estadorespaldo);
	}

	@Override
	public EstadoRespaldo buscarPorId(int id) {

		return repositorio.buscarPorId(id).orElseThrow(() -> new RuntimeException("Estado de Respaldo no encontrado"));
	}

	@Override
	public List<EstadoRespaldo> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		
		repositorio.eliminar(id);

	}

}
