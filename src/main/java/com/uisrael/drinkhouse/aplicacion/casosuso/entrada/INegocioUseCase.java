package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;

public interface INegocioUseCase {

	Negocio crearNegocio(Negocio negocio);

	Negocio actualizarNegocio(Integer id, Negocio negocio);

	Negocio buscarActivo();

	Negocio buscarPorId(Integer id);

}
