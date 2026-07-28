package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetalleOrdenCompraRequestDto {

	@NotNull
	private Long productoId;

	@NotNull
	@Positive
	private Integer cantidad;

	@NotNull
	@Positive
	private BigDecimal precioUnitario;

	private String observaciones;
}
