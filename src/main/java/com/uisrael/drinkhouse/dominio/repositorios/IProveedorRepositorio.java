package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;

public interface IProveedorRepositorio {

	Proveedor guardar(Proveedor proveedor);

	Optional<Proveedor> buscarPorId(Long id);

	List<Proveedor> listarTodos();

	boolean existePorRuc(String ruc);
	
	Optional<Proveedor> buscarPorRuc(String ruc);

	void eliminar(Long id);

	boolean tieneOrdenesAsociadas(Long id);
}
