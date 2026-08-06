package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Venta;

/**
 * Puerto de salida para operaciones de persistencia de ventas.
 */
public interface IVentaRepositorio {

	/**
	 * Busca una venta por su ID.
	 * @param ventaId el ID de la venta
	 * @return Optional con la venta si existe
	 */
	Optional<Venta> buscarPorId(Long ventaId);
}
