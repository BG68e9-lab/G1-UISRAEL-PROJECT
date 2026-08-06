package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para el reporte de consumo de IA mensual.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteConsumoIaMensualDto {

    private Long consumoIaId;
    private Integer negocioId;
    private String negocioNombre;
    private LocalDate periodo;
    private Integer cantidadIdentificaciones;
    private Integer tokensConsumidos;
    private String estadoCuota;
}
