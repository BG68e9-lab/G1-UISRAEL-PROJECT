package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * DTO de respuesta con todos los campos de un Lote de Producto.
 */
@Data
public class LoteProductoResponseDto {

	/** Identificador único del lote. */
	private Long loteId;

	/** ID del producto asociado. */
	private Long productoId;

	/** Nombre del producto asociado. */
	private String productoNombre;

	/** Código de entrada generado (formato LOTE-XXXXXXXX). */
	private String codigoEntrada;

	/** Cantidad inicial con la que se registró el lote. */
	private BigDecimal cantidadInicial;

	/** Cantidad actualmente disponible en el lote. */
	private BigDecimal cantidadDisponible;

	/** Precio de costo por unidad. */
	private BigDecimal precioCosto;

	/** Fecha y hora en que se registró el ingreso del lote. */
	private OffsetDateTime fechaIngreso;

	/** Fecha de vencimiento del lote (puede ser nula). */
	private LocalDate fechaVencimiento;
}
