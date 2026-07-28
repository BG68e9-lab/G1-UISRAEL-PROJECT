package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioRepositorio {

	MovimientoInventario guardar(MovimientoInventario movimiento);

	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);

	/**
	 * Lista todos los movimientos ordenados por fecha descendente.
	 */
	List<MovimientoInventario> listarTodos();

	/**
	 * Busca movimientos por tipo de movimiento.
	 */
	List<MovimientoInventario> buscarPorTipo(String codigoTipo);

	/**
	 * Busca movimientos por lote.
	 */
	List<MovimientoInventario> buscarPorLote(Long loteId);

	/**
	 * Busca un movimiento por su ID.
	 */
	Optional<MovimientoInventario> buscarPorId(Long id);
}
