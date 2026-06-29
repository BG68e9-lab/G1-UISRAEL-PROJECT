package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;

public interface IEstadoOc {
	
	EstadoOc guardar(EstadoOc nuevoEstadoOc);
	
	Optional<EstadoOc> buscarPorId(Integer idEstadoOc);
	
	List<EstadoOc> listarTodos();
	
	void eliminar(Integer idEstadoOc);

}
