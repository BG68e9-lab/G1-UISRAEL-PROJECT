package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NegocioResponseDto {

	private Integer negocioId;
	private String nombre;
	private String ruc;
	private Boolean activo;
	private LocalDateTime creadoEn;
}