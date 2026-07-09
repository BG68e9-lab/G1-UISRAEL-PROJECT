package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimientoInventarioRequestDto {

	@NotBlank
	private String codigoMovimiento;

	@NotNull
	private BigDecimal cantidad;

	private BigDecimal precioUnitario;
}
