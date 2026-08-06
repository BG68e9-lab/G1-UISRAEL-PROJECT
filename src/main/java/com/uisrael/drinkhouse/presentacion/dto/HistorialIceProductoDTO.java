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
public class HistorialIceProductoDTO {

	private Long historialIceId;
	private Long productoId;
	private Boolean aplicaIceAnterior;
	private BigDecimal tarifaIceAnterior;
	private BigDecimal valorEspecificoAnterior;
	private String tipoTarifaAnterior;
	private Boolean aplicaIceNuevo;
	private BigDecimal tarifaIceNueva;
	private BigDecimal valorEspecificoNuevo;
	private String tipoTarifaNuevo;
	private String grupoIce;
	private Boolean esMonofasico;
	private String motivo;
	private String usuarioModificador;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String resolucionSri;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fechaVigencia;
}
