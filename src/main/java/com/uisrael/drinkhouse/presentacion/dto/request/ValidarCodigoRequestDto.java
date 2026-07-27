package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidarCodigoRequestDto {

	@NotBlank
	private String codigoHash;
}
