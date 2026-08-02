package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestablecerPasswordRequestDto {

	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String codigo;

	@NotBlank
	@Size(min = 8, message = "La nueva contrasena debe tener al menos 8 caracteres")
	private String nuevaPassword;

}
