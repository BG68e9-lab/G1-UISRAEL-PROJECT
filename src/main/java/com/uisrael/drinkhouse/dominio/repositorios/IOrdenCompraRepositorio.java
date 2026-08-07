package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

public interface IOrdenCompraRepositorio {

	OrdenCompra guardar(OrdenCompra ordenCompra);

OrdenCompra guardarConRelaciones(OrdenCompra ordenCompra, Long proveedorId);

	Optional<OrdenCompra> buscarPorId(Long id);

	List<OrdenCompra> buscarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta);
}
