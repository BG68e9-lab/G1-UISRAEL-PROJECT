package com.uisrael.drinkhouse.dominio.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoRepositorio {

	Producto guardar(Producto producto);

	Optional<Producto> buscarPorId(Long id);

	List<Producto> listarTodos();

	void eliminar(Long id);

	boolean existePorNombre(String nombre);

	List<Producto> buscarConFiltros(String nombre, String marca, Long tipoProductoId, Long categoriaId);

	/**
	 * Locks product row for update to prevent concurrent modifications.
	 * Uses SELECT FOR UPDATE to acquire pessimistic lock.
	 * @param productoId Product identifier
	 * @return Locked product entity
	 * @throws RecursoNoEncontradoException if product not found
	 */
	Producto lockForUpdate(Long productoId);

	/**
	 * Updates product stock quantity.
	 * @param productoId Product identifier
	 * @param newStock New stock value
	 */
	void actualizarStock(Long productoId, BigDecimal newStock);
}
