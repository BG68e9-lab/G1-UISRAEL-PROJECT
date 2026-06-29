package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;

public class SecuenciaCodigoUseCaseImpl implements ISecuenciaCodigoUseCase {

	private final ISecuenciaCodigoRepositorio repositorio;

	public SecuenciaCodigoUseCaseImpl(ISecuenciaCodigoRepositorio repositorio) {

		this.repositorio = repositorio;
	}

	@Override
	public SecuenciaCodigo guardar(SecuenciaCodigo secuenciacodigo) {

		return repositorio.guardar(secuenciacodigo);
	}

	@Override
	public SecuenciaCodigo buscarPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RuntimeException("Secuencia de Codigo no encontrada"));
	}

	@Override
	public List<SecuenciaCodigo> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		repositorio.eliminar(id);

	}

}
