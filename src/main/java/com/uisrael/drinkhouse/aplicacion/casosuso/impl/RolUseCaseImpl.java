package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.dominio.repositorios.IRolRepositorio;

public class RolUseCaseImpl implements IRolUseCase{
	
	private final IRolRepositorio repositorio;

	public RolUseCaseImpl(IRolRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Rol guardar(Rol nuevoRol) {
		return repositorio.guardar(nuevoRol);
	}

	@Override
	public Rol buscarPorId(Integer idRol) {
		return repositorio.buscarPorId(idRol)
				.orElseThrow(() -> new RuntimeException("Rol no encontrado"));
	}

	@Override
	public List<Rol> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(Integer idRol) {
		repositorio.eliminar(idRol);
	}

}
