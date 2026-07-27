package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuarioRequestDto {

	@NotBlank
	private String nombreCompleto;

	@NotBlank
	@Email
	private String email;

	// Opcional cuando se usa SSO
	private String passwordHash;

	private String proveedorSso;

	private String ssoSubjectId;

	private Integer rolId;
}
