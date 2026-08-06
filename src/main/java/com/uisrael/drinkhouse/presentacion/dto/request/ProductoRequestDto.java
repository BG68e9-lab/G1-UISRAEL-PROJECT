package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoRequestDto {

	@NotBlank
	private String nombre;

	@NotBlank
	private String marca;

	private Long tipoProductoId;

	private String descripcion;

	private Long categoriaId;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private BigDecimal costoPromedio;

	@NotNull
	@DecimalMin(value = "0")
	private BigDecimal margenGanancia;

	private BigDecimal precioVenta;

	private Boolean precioPersonalizado;

	private Integer stockMinimo;

	private Boolean visibleSinStock;

	private Integer negocioId;

	private String origenIdentificacion;
}
