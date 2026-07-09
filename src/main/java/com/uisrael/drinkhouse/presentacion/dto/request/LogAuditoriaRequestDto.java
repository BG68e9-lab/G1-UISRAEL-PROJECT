package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogAuditoriaRequestDto {

	@NotBlank
	private String entidad;

	@NotBlank
	private String entidadId;

	@NotBlank
	private String accion;

	private String detalle;
}
