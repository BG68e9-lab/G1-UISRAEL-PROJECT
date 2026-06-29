package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;

public interface IEstadoRespaldoRepositorio {

	EstadoRespaldo guardar(EstadoRespaldo estadorespaldo);

	Optional<EstadoRespaldo> buscarPorId(int id);

	List<EstadoRespaldo> listarTodos();

	void eliminar(int id);

}
