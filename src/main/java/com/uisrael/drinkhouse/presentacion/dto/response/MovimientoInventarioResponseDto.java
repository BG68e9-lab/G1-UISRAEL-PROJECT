package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * DTO de respuesta para un movimiento de inventario.
 * Incluye identificadores de las entidades relacionadas.
 */
@Data
public class MovimientoInventarioResponseDto {

	private Long movimientoId;
	private String codigoMovimiento;
	private Long productoId;
	private Long loteId;
	private Long tipoMovimientoId;
	private String tipoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private OffsetDateTime creadoEn;
}
