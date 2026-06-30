package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioRepositorio {
	
	Usuario guardar(Usuario nuevoUsuario);
	
	Optional<Usuario> buscarPorId(UUID idUsuario);
	
	List<Usuario> listarTodos();
	
	void eliminar(UUID idUsuario);

}
