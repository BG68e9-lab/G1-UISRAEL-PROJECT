package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;

public interface ISecuenciaCodigoRepositorio {

	SecuenciaCodigo guardar(SecuenciaCodigo secuenciacodigo);

	Optional<SecuenciaCodigo> buscarPorId(int id);

	List<SecuenciaCodigo> listarTodos();

	void eliminar(int id);
}
