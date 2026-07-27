package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;

public interface ITipoMovimientoUseCase {

	TipoMovimiento crearTipoMovimiento(TipoMovimiento tipoMovimiento);

	TipoMovimiento buscarPorId(Integer id);

	List<TipoMovimiento> listarTodos();
}
