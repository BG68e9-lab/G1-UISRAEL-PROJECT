package com.uisrael.drinkhouse.presentacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
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
public class HistorialIvaProductoDTO {

	private Long historialIvaId;
	private Long productoId;
	private BigDecimal tarifaIvaAnterior;
	private String codigoPorcentajeAnterior;
	private String descripcionAnterior;
	private BigDecimal tarifaIvaNueva;
	private String codigoPorcentajeNuevo;
	private String descripcionNueva;
	private String motivo;
	private String usuarioModificador;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String resolucionSri;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaVigencia;
}
