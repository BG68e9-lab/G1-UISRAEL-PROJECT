package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

	private String message;
	private UsuarioResponseDto usuario;
}
