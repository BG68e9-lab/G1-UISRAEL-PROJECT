package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;


import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoUseCase {
	
	LoteProducto guardar(LoteProducto loteProducto);

	LoteProducto buscarPorId(int id);

	List<LoteProducto> listarTodos();

	void eliminar(int id);
}
