package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Rol;

public interface IRolUseCase {
	
	Rol guardar(Rol nuevoRol);
	
	Rol buscarPorId(Integer idRol);
	
	List<Rol> listarTodos();
	
	void eliminar(Integer idRol);

}
