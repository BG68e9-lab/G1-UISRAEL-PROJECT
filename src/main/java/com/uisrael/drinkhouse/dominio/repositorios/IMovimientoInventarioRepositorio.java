package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioRepositorio {

	MovimientoInventario guardar(MovimientoInventario movimiento);

	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);

List<MovimientoInventario> listarTodos();

List<MovimientoInventario> buscarPorTipo(String codigoTipo);

List<MovimientoInventario> buscarPorLote(Long loteId);

Optional<MovimientoInventario> buscarPorId(Long id);
}
