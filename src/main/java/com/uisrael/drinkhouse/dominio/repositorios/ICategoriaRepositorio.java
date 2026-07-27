package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;

public interface ICategoriaRepositorio {

	Categoria guardar(Categoria categoria);

	Optional<Categoria> buscarPorId(Long id);

	List<Categoria> listarTodas();

	void eliminar(Long id);

	boolean existePorNombre(String nombre);

	boolean tieneProductosAsociados(Long id);

}
