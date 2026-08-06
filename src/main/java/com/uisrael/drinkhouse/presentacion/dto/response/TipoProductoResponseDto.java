package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class TipoProductoResponseDto {

	private Long tipoProductoId;
	private Long categoriaId;
	private String categoriaNombre;
	private Integer negocioId;
	private String nombre;
	private String descripcion;
	private Boolean activo;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;
}
