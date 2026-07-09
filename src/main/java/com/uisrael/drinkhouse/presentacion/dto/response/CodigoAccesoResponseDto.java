package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class CodigoAccesoResponseDto {

	private UUID codigoAccesoId;
	private String tipoCodigo;
	private Boolean usado;
	private OffsetDateTime usadoEn;
	private OffsetDateTime expiraEn;
	private OffsetDateTime creadoEn;
}
