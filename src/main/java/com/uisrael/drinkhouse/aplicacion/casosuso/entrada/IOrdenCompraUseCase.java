package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

/**
 * Puerto de entrada para el caso de uso de Órdenes de Compra.
 * Define las operaciones del ciclo de vida de una orden de compra.
 */
public interface IOrdenCompraUseCase {

    /**
     * Crea una nueva orden de compra en estado BORRADOR con sus detalles.
     *
     * @param orden    datos de la orden (debe incluir proveedorId)
     * @param detalles lista de detalles de la orden
     * @return la orden creada con su código de referencia generado
     */
    OrdenCompra crearOrden(OrdenCompra orden, List<DetalleOrdenCompra> detalles);

    /**
     * Actualiza una orden existente en estado BORRADOR.
     *
     * @param id      ID de la orden a actualizar
     * @param orden   nuevos datos de la orden
     * @param detalles nuevos detalles (reemplazan los existentes)
     * @return la orden actualizada
     */
    OrdenCompra actualizarOrden(Long id, OrdenCompra orden, List<DetalleOrdenCompra> detalles);

    /**
     * Envía una orden, cambiando su estado de BORRADOR a ENVIADA.
     *
     * @param id ID de la orden a enviar
     * @return la orden con estado ENVIADA
     */
    OrdenCompra enviarOrden(Long id);

    /**
     * Recibe una orden, cambiando su estado de ENVIADA a RECIBIDA.
     * Genera lotes de producto por cada detalle e incrementa el stock.
     *
     * @param id ID de la orden a recibir
     * @return la orden con estado RECIBIDA
     */
    OrdenCompra recibirOrden(Long id);

    /**
     * Anula una orden en estado BORRADOR o ENVIADA.
     *
     * @param id ID de la orden a anular
     * @return la orden con estado ANULADA
     */
    OrdenCompra anularOrden(Long id);

    /**
     * Busca una orden por su ID.
     *
     * @param id ID de la orden
     * @return la orden encontrada
     */
    OrdenCompra buscarPorId(Long id);

    /**
     * Lista órdenes con filtros opcionales.
     *
     * @param estado código de estado (opcional)
     * @param desde  fecha de inicio del rango (opcional)
     * @param hasta  fecha de fin del rango (opcional)
     * @return lista de órdenes que cumplen los filtros
     */
    List<OrdenCompra> listarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta);
}
