package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;

public interface ICodigoAccesoUseCase {
	
	CodigoAcceso guardar(CodigoAcceso nuevoCodigoAcceso);
	
	CodigoAcceso buscarPorId(UUID idCodigoAcceso);
	
	List<CodigoAcceso> listarTodos();
	
	void eliminar(UUID idCodigoAcceso);

}
