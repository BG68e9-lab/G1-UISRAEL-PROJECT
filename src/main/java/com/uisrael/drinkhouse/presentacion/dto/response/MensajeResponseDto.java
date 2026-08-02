package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.Data;

@Data
public class MensajeResponseDto {

	private String mensaje;

	public MensajeResponseDto() {
	}

	public MensajeResponseDto(String mensaje) {
		this.mensaje = mensaje;
	}

}
