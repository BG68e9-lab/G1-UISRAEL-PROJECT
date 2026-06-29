package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;


import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;

public interface ISecuenciaCodigoUseCase {
	
	SecuenciaCodigo guardar(SecuenciaCodigo secuenciacodigo);

	SecuenciaCodigo buscarPorId(int id);

	List<SecuenciaCodigo> listarTodos();

	void eliminar(int id);


}
