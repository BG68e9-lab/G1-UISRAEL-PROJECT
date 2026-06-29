package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioUseCase {

	Usuario guardar(Usuario nuevoUsuario);
	
	Usuario buscarPorId(Integer idUsuario);
	
	List<Usuario> listarTodos();
	
	void eliminar(Integer idUsuario);
}
