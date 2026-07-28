package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarCantidadLoteRequestDto {

	@NotNull
	@Min(0)
	private Integer cantidadDisponible;
}
