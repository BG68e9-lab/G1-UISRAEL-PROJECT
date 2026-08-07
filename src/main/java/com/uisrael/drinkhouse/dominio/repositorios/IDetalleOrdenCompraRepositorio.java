package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;

public interface IDetalleOrdenCompraRepositorio {

	DetalleOrdenCompra guardar(DetalleOrdenCompra detalle);

DetalleOrdenCompra guardarConOrdenCompraId(DetalleOrdenCompra detalle, Long ordenCompraId);

	List<DetalleOrdenCompra> buscarPorOrdenCompraId(Long ordenCompraId);

	void eliminarPorOrdenCompraId(Long ordenCompraId);
}
