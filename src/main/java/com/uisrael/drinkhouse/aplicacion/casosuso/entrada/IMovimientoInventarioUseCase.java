package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioUseCase {

	MovimientoInventario guardar(MovimientoInventario movimientoInventario);

	MovimientoInventario buscarPorId(Long id);

	List<MovimientoInventario> listarTodo(String tipo);

	void eliminar(Long id);
}
