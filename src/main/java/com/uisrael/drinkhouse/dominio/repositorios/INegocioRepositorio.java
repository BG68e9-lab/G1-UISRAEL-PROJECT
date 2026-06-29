package com.uisrael.drinkhouse.dominio.repositorios;


import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;

public interface INegocioRepositorio {
	
	Negocio guardar(Negocio nuevoNegocio);
	
	Optional<Negocio> buscarporId(Integer idNegocio);
	
	List<Negocio> listarTodos();
	
	void eliminar(Integer idNegocio);

}
