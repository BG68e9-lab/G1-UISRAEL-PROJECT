package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ProductoPrecioHistoricoResponseDto {

	private Long id;
	private Long productoId;
	private BigDecimal costoPromedio;
	private BigDecimal margenGanancia;
	private BigDecimal precioVenta;
	private BigDecimal ivaPorcentajeAplicado;
	private String iceTipoAplicado;
	private BigDecimal iceValorAplicado;
	private BigDecimal precioFinalConImpuestos;
	private OffsetDateTime vigenteDesde;
	private OffsetDateTime vigenteHasta;
	private String motivo;
}
