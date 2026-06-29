package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MovimientoInventarioRequestDto {
	
	private Long movimientoId;
	@NotBlank
	private Integer negocioId;
	@NotBlank
	private Integer tipoMovimientoId;
	@NotBlank
	private Long productoId; //
	@NotBlank
	private Long loteId;
	@NotBlank
	private String codigoMovimiento;
	@NotBlank
	private BigDecimal cantidad;
	@NotBlank
	private BigDecimal precioUnitario;
	@NotBlank
	private UUID usuarioId;

}
