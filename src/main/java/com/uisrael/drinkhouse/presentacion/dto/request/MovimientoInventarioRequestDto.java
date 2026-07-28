package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para registrar un movimiento de inventario.
 * Cubre entradas, salidas y ajustes de stock.
 */
@Data
public class MovimientoInventarioRequestDto {

	/** ID del producto afectado por el movimiento. */
	@NotNull(message = "El productoId es obligatorio")
	private Long productoId;

	/** ID del lote (requerido para movimientos de tipo SALIDA). */
	private Long loteId;

	/** ID del tipo de movimiento (ENTRADA, SALIDA, AJUSTE). */
	@NotNull(message = "El tipoMovimientoId es obligatorio")
	private Long tipoMovimientoId;

	/** 
	 * Cantidad del movimiento.
	 * Para ENTRADA y SALIDA debe ser positiva.
	 * Para AJUSTE puede ser positiva (aumentar) o negativa (disminuir).
	 */
	@NotNull(message = "La cantidad es obligatoria")
	private BigDecimal cantidad;

	/** Precio unitario del producto en el movimiento (opcional). */
	private BigDecimal precioUnitario;
}
