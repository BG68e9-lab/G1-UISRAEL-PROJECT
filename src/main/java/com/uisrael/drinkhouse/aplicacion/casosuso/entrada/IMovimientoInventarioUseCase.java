package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

/**
 * Puerto de entrada para el módulo de movimientos de inventario.
 * Cubre registro de entradas, salidas y ajustes de stock.
 */
public interface IMovimientoInventarioUseCase {

	/**
	 * Registra un movimiento de inventario (ENTRADA, SALIDA o AJUSTE).
	 * Aplica la lógica de negocio correspondiente al tipo de movimiento.
	 *
	 * @param productoId       ID del producto afectado
	 * @param loteId           ID del lote (requerido para SALIDA)
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param movimiento       datos del movimiento (cantidad, precioUnitario)
	 * @return el movimiento persistido con codigoMovimiento generado
	 */
	MovimientoInventario registrar(Long productoId, Long loteId, Long tipoMovimientoId,
			MovimientoInventario movimiento);

	/**
	 * Consulta movimientos de un producto con filtros opcionales.
	 *
	 * @param productoId ID del producto
	 * @param tipo       código del tipo de movimiento (opcional)
	 * @param desde      fecha/hora de inicio del rango (opcional)
	 * @param hasta      fecha/hora de fin del rango (opcional)
	 * @return lista de movimientos ordenada por creadoEn descendente
	 */
	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);
}
