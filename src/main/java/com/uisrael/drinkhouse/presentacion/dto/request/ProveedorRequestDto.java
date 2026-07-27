package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProveedorRequestDto {

	@NotBlank
	@Pattern(regexp = "\\d{13}", message = "El RUC debe tener exactamente 13 dígitos numéricos")
	private String ruc;

	@NotBlank
	private String razonSocial;

	private String direccion;

	private String telefono;

	@NotBlank
	@Email
	private String email;
}
