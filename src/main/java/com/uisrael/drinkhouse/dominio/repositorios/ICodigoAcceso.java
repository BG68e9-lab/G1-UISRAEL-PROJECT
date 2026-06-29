package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;

public interface ICodigoAcceso {
	
	CodigoAcceso guardar(CodigoAcceso nuevoCodigoAcceso);
	
	Optional<CodigoAcceso> buscarPorId(UUID idCodigoAcceso);
	
	List<CodigoAcceso> listarTodos();
	
	void eliminar(UUID idCodigoAcceso);

}
