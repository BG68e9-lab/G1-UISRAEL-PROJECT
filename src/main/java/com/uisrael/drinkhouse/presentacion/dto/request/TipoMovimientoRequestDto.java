package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TipoMovimientoRequestDto {

	@NotBlank
	private String codigo;

	@NotBlank
	private String prefijoCodigo;

	private String descripcion;
}
