package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de entrada para la creación de un Lote de Producto.
 * Contiene las validaciones Jakarta requeridas por el Requisito 5.1.
 */
@Data
public class LoteProductoRequestDto {

	/** ID del producto al que pertenece este lote. Requerido. */
	@NotNull(message = "El productoId es obligatorio")
	private Long productoId;

	/**
	 * Cantidad inicial del lote. Debe ser mayor a cero.
	 * Requisito 5.1, 5.5
	 */
	@NotNull(message = "La cantidadInicial es obligatoria")
	@DecimalMin(value = "0", inclusive = false, message = "La cantidadInicial debe ser mayor a cero")
	private BigDecimal cantidadInicial;

	/**
	 * Precio de costo por unidad del lote. Debe ser mayor a cero.
	 * Requisito 5.1
	 */
	@NotNull(message = "El precioCosto es obligatorio")
	@DecimalMin(value = "0", inclusive = false, message = "El precioCosto debe ser mayor a cero")
	private BigDecimal precioCosto;

	/** Fecha de vencimiento del lote. Opcional. */
	private LocalDate fechaVencimiento;
}
