package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;

public interface ICategoriaRepositorio {
	
	Categoria guardar(Categoria nuevoCategoria);
	
	Optional<Categoria> buscarPorId(Integer idCategoria);
	
	List<Categoria> listarTodos();
	
	void eliminar(Integer idCategoria);

}
