package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;

public interface ISecuenciaCodigoRepositorio {

	Optional<SecuenciaCodigo> buscarPorNegocioYTipo(Integer negocioId, Integer tipoMovimientoId);

	SecuenciaCodigo guardar(SecuenciaCodigo secuenciaCodigo);

	List<SecuenciaCodigo> listarTodas();

	List<SecuenciaCodigo> listarPorNegocio(Integer negocioId);

	void eliminar(SecuenciaCodigo secuenciaCodigo);
}
