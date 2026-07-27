package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoUseCase {

	Producto crearProducto(Producto producto);

	Producto actualizarProducto(Long id, Producto producto);

	Producto buscarPorId(Long id);

	List<Producto> listarProductos();

	List<Producto> buscarConFiltros(String nombre, String marca, String tipo, Long categoriaId);

	void eliminarProducto(Long id);
}
