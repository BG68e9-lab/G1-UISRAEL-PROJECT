package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoUseCase {
	
	
	Producto crear(Producto producto);
	Producto actualizar(int id, Producto producto);
	Producto buscarPorId(int id);
	List<Producto> listar();
	void eliminar(int id);

	/**
	 * Filtra productos por nombre y/o marca (coincidencia parcial, sin
	 * distinguir mayusculas/minusculas). Si un filtro viene nulo o en blanco,
	 * no se aplica.
	 */
	List<Producto> buscar(String nombre, String marca);

}
