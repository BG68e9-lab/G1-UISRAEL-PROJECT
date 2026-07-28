package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioRepositorio {

	MovimientoInventario guardar(MovimientoInventario movimientoInventario);

	Optional<MovimientoInventario> buscarPorId(Long id);

	List<MovimientoInventario> listarTodo(String tipo);

	void eliminar(Long id);
}
