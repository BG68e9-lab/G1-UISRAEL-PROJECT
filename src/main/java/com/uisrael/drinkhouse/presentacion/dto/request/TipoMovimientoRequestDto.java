package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TipoMovimientoRequestDto {
	
	private Integer tipoMovimientoId;
	@NotBlank
	private String codigo;
	@NotBlank
	private String prefijoCodigo;
	@NotBlank
	private String descripcion;


}
