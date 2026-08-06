package com.uisrael.drinkhouse.presentacion.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPrecioProductoDTO {

	private Long historialPrecioId;
	private Long productoId;
	private BigDecimal costoPromedioAnterior;
	private BigDecimal margenGananciaAnterior;
	private BigDecimal precioVentaAnterior;
	private BigDecimal costoPromedioNuevo;
	private BigDecimal margenGananciaNuevo;
	private BigDecimal precioVentaNuevo;
	private BigDecimal variacionCostoPorcentaje;
	private BigDecimal variacionPrecioVentaPorcentaje;
	private String motivo;
	private String usuarioModificador;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String facturaRelacionada;
	private Long ordenCompraId;
}
