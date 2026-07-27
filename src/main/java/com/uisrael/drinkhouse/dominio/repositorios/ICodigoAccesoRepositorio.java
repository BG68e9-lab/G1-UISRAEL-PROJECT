package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;

public interface ICodigoAccesoRepositorio {

	CodigoAcceso guardar(CodigoAcceso codigoAcceso);

	Optional<CodigoAcceso> buscarPorHash(String codigoHash);
}
