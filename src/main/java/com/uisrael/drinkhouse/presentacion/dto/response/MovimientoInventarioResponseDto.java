package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class MovimientoInventarioResponseDto {

	private Long movimientoId;
	private String codigoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private OffsetDateTime creadoEn;
}
