package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;

public class ProductoUseCaseImpl implements IProductoUseCase{
	
	
	
	private final IProductoRepositorio repositorio;
	
	

	public ProductoUseCaseImpl(IProductoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public Producto crear(Producto producto) {
	
		return repositorio.guardar(producto);
	}

	@Override
	public Producto buscarPorId(int id) {

		return repositorio.buscarPorId(id)
				.orElseThrow(()->new RuntimeException("Producto no encontrado"));
	}

	@Override
	public List<Producto> listar() {

		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		
		repositorio.eliminar(id);
		
	}
	
	

}
