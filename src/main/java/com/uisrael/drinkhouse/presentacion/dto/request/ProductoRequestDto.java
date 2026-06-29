package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductoRequestDto {
	
	private Integer productoId;
	@NotBlank
	private Integer negocioId;
	@NotBlank
	private Integer categoriaId;
	@NotBlank
	private String nombre;
	@NotBlank
	private String marca;
	@NotBlank
	private String tipo;
	@NotBlank
	private String descripcion;
	@NotBlank
	private BigDecimal costoPromedio;
	@NotBlank
	private BigDecimal margenGanancia;
	@NotBlank
	private BigDecimal precioVenta;
	@NotBlank
	private BigDecimal precioPersonalizado;
	@NotBlank
	private Integer stockActual;	
	@NotBlank
	private Integer stockMinimo;
	@NotBlank
	private Boolean visibleSinStock;
	@NotBlank
	private String origenIdentificacion;
	
	

}
