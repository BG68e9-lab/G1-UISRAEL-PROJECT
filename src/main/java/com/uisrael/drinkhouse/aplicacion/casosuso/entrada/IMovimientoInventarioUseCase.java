package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioUseCase {

	MovimientoInventario guardar(MovimientoInventario movimientoinventario);

	MovimientoInventario buscarPorId(int id);

	List<MovimientoInventario> listarTodo();

	void eliminar(int id);
}
