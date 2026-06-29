package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;

public interface ITipoMovimientoRepositorio {
	TipoMovimiento guardar(TipoMovimiento tipomovimiento);

	Optional<TipoMovimiento> buscarPorId(int id);

	List<TipoMovimiento> listarTodos();

	void eliminar(int id);

}
