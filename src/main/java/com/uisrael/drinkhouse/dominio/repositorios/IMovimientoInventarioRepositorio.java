package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioRepositorio {

	MovimientoInventario guardar(MovimientoInventario movimiento);

	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);
}
