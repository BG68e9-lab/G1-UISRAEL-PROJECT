package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumoIaMensual {

    private Long consumoIaId;
    private Integer negocioId;
    
    private LocalDate periodo;
    private Long totalTokensInput;
    private Long totalTokensOutput;
    private BigDecimal costoEstimadoUsd;
}
