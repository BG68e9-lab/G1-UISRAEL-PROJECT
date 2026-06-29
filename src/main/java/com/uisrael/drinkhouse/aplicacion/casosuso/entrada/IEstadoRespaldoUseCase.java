package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;


import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;

public interface IEstadoRespaldoUseCase {

	EstadoRespaldo guardar(EstadoRespaldo estadorespaldo);

	EstadoRespaldo buscarPorId(int id);

	List<EstadoRespaldo> listarTodos();

	void eliminar(int id);
}
