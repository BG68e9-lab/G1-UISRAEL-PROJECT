package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoteProductoRequestDto {

	private Integer loteId;
	@NotBlank
	private Integer negocioId;
	@NotBlank
	private Integer ordenCompraId;
	@NotBlank
	private String codigoEntrada;
	@NotBlank
	private Integer cantidadInicial;
	@NotBlank
	private Integer cantidadDisponible;
	@NotBlank
	private BigDecimal precioCosto;
	@NotBlank
	private OffsetDateTime fechaIngreso;
	@NotBlank
	private OffsetDateTime fechaVencimiento;
	@NotBlank
	private Integer estadoRespaldoId;
	@NotBlank
	private String registradoPor;

}
