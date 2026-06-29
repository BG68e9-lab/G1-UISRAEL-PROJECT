package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RolRequestDto {
	

	@NotBlank
	private String nombre;
	@NotBlank
	private String descripcion;

}
