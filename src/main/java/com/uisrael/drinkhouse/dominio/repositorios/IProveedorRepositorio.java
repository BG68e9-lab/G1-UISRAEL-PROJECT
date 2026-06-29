package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;

public interface IProveedorRepositorio {

	Proveedor guardar(Proveedor proveedor);

	Optional<Proveedor> buscarPorId(int id);

	List<Proveedor> listarTodos();

	void eliminar(int id);
}
