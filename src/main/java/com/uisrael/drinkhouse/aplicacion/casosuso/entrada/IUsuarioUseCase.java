package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioUseCase {

	Usuario guardar(Usuario nuevoUsuario);
	
	Usuario buscarPorId(UUID idUsuario);
	
	List<Usuario> listarTodos();
	
	void eliminar(UUID idUsuario);
}
