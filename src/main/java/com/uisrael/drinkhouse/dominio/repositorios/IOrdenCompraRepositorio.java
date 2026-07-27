package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

public interface IOrdenCompraRepositorio {

	OrdenCompra guardar(OrdenCompra ordenCompra);

	/**
	 * Guarda la orden de compra estableciendo las relaciones JPA con
	 * el proveedor y el estado (por código) directamente en el adaptador.
	 */
	OrdenCompra guardarConRelaciones(OrdenCompra ordenCompra, Long proveedorId);

	Optional<OrdenCompra> buscarPorId(Long id);

	List<OrdenCompra> buscarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta);
}
