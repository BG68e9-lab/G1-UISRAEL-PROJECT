package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoRepositorio {

	Producto guardar(Producto producto);

	Optional<Producto> buscarPorId(int id);

	List<Producto> listarTodos();

	void eliminar(int id);
}
