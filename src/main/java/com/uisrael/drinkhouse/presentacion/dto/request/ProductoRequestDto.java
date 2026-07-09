package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoRequestDto {

	@NotBlank
	private String nombre;

	private String marca;

	private String tipo;

	private String descripcion;

	@NotNull
	private BigDecimal costoPromedio;

	private BigDecimal margenGanancia;

	@NotNull
	private BigDecimal precioVenta;

	@NotNull
	private Boolean precioPersonalizado;

	@NotNull
	private Integer stockActual;

	@NotNull
	private Integer stockMinimo;

	@NotNull
	private Boolean visibleSinStock;

	private String origenIdentificacion;
}
