package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioRepositorio {

	MovimientoInventario guardar(MovimientoInventario movimientoinventario);

	Optional<MovimientoInventario> buscarPorId(int id);

	List<MovimientoInventario> listarTodo();

	void eliminar(int id);

}
