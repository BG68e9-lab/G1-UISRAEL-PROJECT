package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;

public class ProveedorUseCaseImpl implements IProveedorUseCase {

	private final IProveedorRepositorio repositorio;

	public ProveedorUseCaseImpl(IProveedorRepositorio repositorio) {

		this.repositorio = repositorio;
	}

	@Override
	public Proveedor crear(Proveedor proveedor) {

		return repositorio.guardar(proveedor);
	}

	@Override
	public Proveedor buscarPorId(int id) {

		return repositorio.buscarPorId(id).orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
	}

	@Override
	public List<Proveedor> listar() {

		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {

		repositorio.eliminar(id);

	}

}
