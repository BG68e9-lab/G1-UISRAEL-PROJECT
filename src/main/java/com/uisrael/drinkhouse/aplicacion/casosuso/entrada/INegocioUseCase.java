package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;

public interface INegocioUseCase {
	
	Negocio guardar(Negocio nuevoNegocio);
	
	Negocio buscarporId(Integer idNegocio);
	
	List<Negocio> listarTodos();
	
	void eliminar(Integer idNegocio);

}
