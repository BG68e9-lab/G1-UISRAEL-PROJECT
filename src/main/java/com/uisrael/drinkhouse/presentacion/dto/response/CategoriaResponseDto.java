package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class CategoriaResponseDto {

	private Long categoriaId;
	private Integer negocioId;
	private String nombre;
	private BigDecimal margenGananciaPct;
	private Boolean activo;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;
}
