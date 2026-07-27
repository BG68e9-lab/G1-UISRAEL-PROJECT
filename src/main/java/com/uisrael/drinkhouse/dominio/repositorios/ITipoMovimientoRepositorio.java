package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;

public interface ITipoMovimientoRepositorio {

	TipoMovimiento guardar(TipoMovimiento tipoMovimiento);

	Optional<TipoMovimiento> buscarPorId(Integer id);

	List<TipoMovimiento> listarTodos();

	boolean existePorCodigo(String codigo);

	Optional<TipoMovimiento> buscarPorCodigo(String codigo);
}
