package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;

public interface IDetalleOrdenCompraRepositorio {

	DetalleOrdenCompra guardar(DetalleOrdenCompra detalle);

	/**
	 * Guarda el detalle asociándolo a la orden de compra mediante su ID JPA.
	 */
	DetalleOrdenCompra guardarConOrdenCompraId(DetalleOrdenCompra detalle, Long ordenCompraId);

	List<DetalleOrdenCompra> buscarPorOrdenCompraId(Long ordenCompraId);

	void eliminarPorOrdenCompraId(Long ordenCompraId);
}
