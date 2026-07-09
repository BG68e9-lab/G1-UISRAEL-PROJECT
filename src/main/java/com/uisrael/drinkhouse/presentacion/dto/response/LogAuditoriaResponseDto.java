package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class LogAuditoriaResponseDto {

	private Long logId;
	private String entidad;
	private String entidadId;
	private String accion;
	private String detalle;
	private OffsetDateTime creadoEn;
}
