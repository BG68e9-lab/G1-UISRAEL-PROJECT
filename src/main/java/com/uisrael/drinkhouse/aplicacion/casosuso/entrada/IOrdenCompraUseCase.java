package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

public interface IOrdenCompraUseCase {

    OrdenCompra crearOrden(OrdenCompra orden, List<DetalleOrdenCompra> detalles);

    OrdenCompra actualizarOrden(Long id, OrdenCompra orden, List<DetalleOrdenCompra> detalles);

    OrdenCompra enviarOrden(Long id);

    OrdenCompra recibirOrden(Long id);

    OrdenCompra anularOrden(Long id);

    OrdenCompra buscarPorId(Long id);

    List<OrdenCompra> listarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta);
}
