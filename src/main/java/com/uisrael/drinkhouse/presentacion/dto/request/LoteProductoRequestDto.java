package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoteProductoRequestDto {

@NotNull(message = "El productoId es obligatorio")
	private Long productoId;

@NotNull(message = "La cantidadInicial es obligatoria")
	@DecimalMin(value = "0", inclusive = false, message = "La cantidadInicial debe ser mayor a cero")
	private BigDecimal cantidadInicial;

@NotNull(message = "El precioCosto es obligatorio")
	@DecimalMin(value = "0", inclusive = false, message = "El precioCosto debe ser mayor a cero")
	private BigDecimal precioCosto;

private LocalDate fechaVencimiento;
}
