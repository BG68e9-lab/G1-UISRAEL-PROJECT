package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovimientoInventarioRequestDto {

	@NotBlank
	private String tipo;

	@NotNull
	private Long productoId;

	private Long loteId;

	/**
	 * Para ENTRADA/SALIDA debe ser positiva; para AJUSTE puede ser positiva o
	 * negativa (pero no cero). Esto se valida en el caso de uso, no aqui,
	 * porque depende del valor de "tipo".
	 */
	@NotNull
	private Integer cantidad;

	private BigDecimal precioUnitario;

	private String descripcion;
}
