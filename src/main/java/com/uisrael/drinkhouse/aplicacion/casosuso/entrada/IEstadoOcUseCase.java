
package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;

public interface IEstadoOcUseCase {

	EstadoOc guardar(EstadoOc nuevoEstadoOc);
	
	EstadoOc buscarPorId(Integer idEstadoOc);
	
	List<EstadoOc> listarTodos();
	
	void eliminar(Integer idEstadoOc);
}
