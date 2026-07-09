package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class CategoriaResponseDto {

	private Integer categoriaId;
	private String nombre;
	private BigDecimal margenGananciaPct;
	private Boolean activo;
}
