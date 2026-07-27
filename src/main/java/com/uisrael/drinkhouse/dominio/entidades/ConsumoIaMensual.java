package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que registra el consumo mensual de tokens de IA por negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumoIaMensual {

    private Long consumoIaId;
    private Integer negocioId;
    /** Período: primer día del mes (ej: 2026-07-01) */
    private LocalDate periodo;
    private Long totalTokensInput;
    private Long totalTokensOutput;
    private BigDecimal costoEstimadoUsd;
}
