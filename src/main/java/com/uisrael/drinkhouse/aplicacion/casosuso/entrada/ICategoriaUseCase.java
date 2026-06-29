package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;

public interface ICategoriaUseCase {
	
	Categoria guardar(Categoria nuevoCategoria);
	
	Categoria buscarPorId(Integer idCategoria);
	
	List<Categoria> listarTodos();
	
	void eliminar(Integer idCategoria);

}
