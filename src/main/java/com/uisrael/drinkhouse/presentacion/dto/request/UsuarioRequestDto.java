package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDto {

	@NotBlank
	private String nombres;

	@NotBlank
	private String apellidos;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String passwordHash;

	private String proveedorSso;

	private String ssoSubjectId;
}
