package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrdenCompraRequestDto {

	@NotNull
	private Long proveedorId;

	private Integer negocioId;

	@NotEmpty
	@Valid
	private List<DetalleOrdenCompraRequestDto> detalles;

	@Size(max = 500)
	private String observaciones;

	private String usuarioCreacion;
}
