package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoteProductoRequestDto {

	@NotBlank
	private String codigoEntrada;

	@NotNull
	private BigDecimal cantidadInicial;

	@NotNull
	private BigDecimal cantidadDisponible;

	@NotNull
	private BigDecimal precioCosto;

	private LocalDate fechaVencimiento;
}