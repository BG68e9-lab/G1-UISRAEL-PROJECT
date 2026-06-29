package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;

public class LoteProductoUseCaseImpl implements ILoteProductoUseCase {

	private final ILoteProductoRepositorio repositorio;

	public LoteProductoUseCaseImpl(ILoteProductoRepositorio repositorio) {
		super();
		this.repositorio = repositorio;
	}

	@Override
	public LoteProducto guardar(LoteProducto loteProducto) {
		// TODO Auto-generated method stub
		return repositorio.guardar(loteProducto);
	}

	@Override
	public LoteProducto buscarPorId(int id) {
		// TODO Auto-generated method stub
		return repositorio.buscarPorId(id).orElseThrow(() -> new RuntimeException("Lote Producto no encontrado"));
	}

	@Override
	public List<LoteProducto> listarTodos() {
		// TODO Auto-generated method stub
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {

		repositorio.eliminar(id);

	}

}
