package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.Data;

@Data
public class LoginResponseDto {

	private String message;
	private UsuarioLoginDto usuario;

	public LoginResponseDto() {
	}

	public LoginResponseDto(String message, UsuarioLoginDto usuario) {
		this.message = message;
		this.usuario = usuario;
	}

}
