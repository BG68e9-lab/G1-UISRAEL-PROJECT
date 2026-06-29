package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioRepositorio {
	
	Usuario guardar(Usuario nuevoUsuario);
	
	Optional<Usuario> buscarPorId(Integer idUsuario);
	
	List<Usuario> listarTodos();
	
	void eliminar(Integer idUsuario);

}
