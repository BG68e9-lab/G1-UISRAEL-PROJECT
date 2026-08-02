package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TasaIvaRequestDto {

	@NotNull
	@DecimalMin(value = "0", message = "El porcentaje de IVA no puede ser negativo")
	private BigDecimal porcentaje;

	private String motivo;
}
