package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SecuenciaCodigoRequestDto {
	
	private Integer negocioId;
	@NotBlank
	private Integer tipoMovimientoId;
	@NotBlank
	private Integer ultimoNumero;

}
