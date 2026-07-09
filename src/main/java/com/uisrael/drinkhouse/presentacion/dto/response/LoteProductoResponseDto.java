package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class LoteProductoResponseDto {

	private Long loteId;
	private String codigoEntrada;
	private BigDecimal cantidadInicial;
	private BigDecimal cantidadDisponible;
	private BigDecimal precioCosto;
	private OffsetDateTime fechaIngreso;
	private LocalDate fechaVencimiento;
	private OffsetDateTime creadoEn;
}
