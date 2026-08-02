package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class TasaIvaResponseDto {

	private Long tasaIvaId;
	private BigDecimal porcentaje;
	private OffsetDateTime vigenteDesde;
	private OffsetDateTime vigenteHasta;
	private String motivo;
}
