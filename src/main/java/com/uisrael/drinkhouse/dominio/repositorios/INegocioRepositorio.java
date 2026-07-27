package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;

public interface INegocioRepositorio {

	Negocio guardar(Negocio negocio);

	Optional<Negocio> buscarPorId(Integer id);

	Optional<Negocio> buscarActivo();

	boolean existePorRuc(String ruc);

}
