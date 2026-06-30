package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Rol;

public interface IRolRepositorio {
	
	Rol guardar(Rol nuevoRol);
	
	Optional<Rol> buscarPorId(Integer idRol);
	
	List<Rol> listarTodos();
	
	void eliminar(Integer idRol);

}
