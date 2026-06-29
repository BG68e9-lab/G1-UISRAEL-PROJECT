package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoUseCase {
	
	
	Producto crear(Producto producto);
	Producto buscarPorId(int id);
	List<Producto> listar();
	void eliminar(int id);

}
