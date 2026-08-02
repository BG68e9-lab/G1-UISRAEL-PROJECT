package com.uisrael.drinkhouse.presentacion.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class UsuarioLoginDto {

	private UUID usuarioId;
	private String email;
	private String nombreCompleto;
	private Integer negocioId;
	private Integer rolId;
	private String rolNombre;

}
