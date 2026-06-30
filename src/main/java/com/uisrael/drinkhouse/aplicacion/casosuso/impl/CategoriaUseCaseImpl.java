package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;

public class CategoriaUseCaseImpl implements ICategoriaUseCase{
	
	private final ICategoriaRepositorio repositorio;

	public CategoriaUseCaseImpl(ICategoriaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Categoria guardar(Categoria nuevoCategoria) {
		return repositorio.guardar(nuevoCategoria);
	}

	@Override
	public Categoria buscarPorId(Integer idCategoria) {
		return repositorio.buscarPorId(idCategoria)
				.orElseThrow(() -> new RuntimeException("Categoria no encontrada"));
	}

	@Override
	public List<Categoria> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(Integer idCategoria) {
		repositorio.eliminar(idCategoria);
	}

}
