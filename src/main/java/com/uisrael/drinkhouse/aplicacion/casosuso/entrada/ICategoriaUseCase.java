package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;

public interface ICategoriaUseCase {

	Categoria crearCategoria(Categoria categoria);

	Categoria actualizarCategoria(Long id, Categoria categoria);

	Categoria buscarPorId(Long id);

	List<Categoria> listarCategorias();

	void eliminarCategoria(Long id);

}
