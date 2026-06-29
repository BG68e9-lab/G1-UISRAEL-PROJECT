package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;


import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;

public interface ITipoMovimientoUseCase {

	
	TipoMovimiento guardar(TipoMovimiento tipomovimiento);

	TipoMovimiento buscarPorId(int id);

	List<TipoMovimiento> listarTodos();

	void eliminar(int id);
}
