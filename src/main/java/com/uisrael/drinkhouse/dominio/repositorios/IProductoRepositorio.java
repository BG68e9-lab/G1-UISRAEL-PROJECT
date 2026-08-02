package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoRepositorio {

	Producto guardar(Producto producto);

	/**
	 * Actualiza los campos editables de un producto existente. No toca
	 * stockActual (se maneja via movimientos de inventario) ni las relaciones
	 * de negocio/categoria (el dominio Producto todavia no las expone).
	 */
	Producto actualizar(int id, Producto producto);

	Optional<Producto> buscarPorId(int id);

	List<Producto> listarTodos();

	void eliminar(int id);
}
