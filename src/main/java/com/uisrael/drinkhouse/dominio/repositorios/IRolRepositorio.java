package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Rol;

public interface IRolRepositorio {

	Rol guardar(Rol rol);

	Optional<Rol> buscarPorId(Integer id);

	List<Rol> listarTodos();

	boolean existePorNombre(String nombre);

}
