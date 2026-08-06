package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class UsuarioResponseDto {

	private UUID usuarioId;
	private Integer negocioId;
	private Integer rolId;
	private String nombreCompleto;
	private String email;
	private String proveedorSso;
	private String estadoCuenta;
	private OffsetDateTime activadoEn;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;
}
