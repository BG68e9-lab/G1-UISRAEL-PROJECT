package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorRequestDto {

	@NotBlank
	private String ruc;

	@NotBlank
	private String razonSocial;

	private String direccion;

	private String telefono;

	private String email;
}
