package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;

public interface IEstadoOcRepositorio {

	EstadoOc guardar(EstadoOc estadoOc);

	Optional<EstadoOc> buscarPorNombre(String nombre);

	Optional<EstadoOc> buscarPorId(Integer id);

	List<EstadoOc> listarTodos();

	void eliminar(Integer id);
}
