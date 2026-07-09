package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoriaRequestDto {

	@NotBlank
	private String nombre;

	private BigDecimal margenGananciaPct;

	@NotNull
	private Boolean activo;
}
