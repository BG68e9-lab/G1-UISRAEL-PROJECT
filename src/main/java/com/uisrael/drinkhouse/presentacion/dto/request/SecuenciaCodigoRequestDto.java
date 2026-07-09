package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SecuenciaCodigoRequestDto {

	@NotNull
	private Long ultimoNumero;
}
