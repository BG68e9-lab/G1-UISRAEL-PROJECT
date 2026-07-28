package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LoteProductoRequestDto {

	@NotNull
	private Long productoId;

	@NotNull
	@Positive
	private Integer cantidadInicial;

	private Integer cantidadDisponible;

	@NotNull
	private BigDecimal precioCosto;

	private LocalDate fechaIngreso;

	@NotNull
	private LocalDate fechaVencimiento;

	private String usuarioCreacion;

	private Long ordenCompraId;
}
