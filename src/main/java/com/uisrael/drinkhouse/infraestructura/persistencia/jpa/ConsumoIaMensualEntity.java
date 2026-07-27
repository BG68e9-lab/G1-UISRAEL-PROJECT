package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que registra el consumo mensual de tokens de IA por negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "consumo_ia_mensual")
public class ConsumoIaMensualEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consumo_ia_id")
    private Long consumoIaId;

    @ManyToOne
    @JoinColumn(name = "negocio_id", nullable = false)
    private NegocioEntity negocio;

    /** Período de consumo (primer día del mes, ej: 2026-07-01) */
    @Column(name = "periodo", nullable = false)
    private LocalDate periodo;

    @Column(name = "total_tokens_input", nullable = false)
    private Long totalTokensInput;

    @Column(name = "total_tokens_output", nullable = false)
    private Long totalTokensOutput;

    @Column(name = "costo_estimado_usd", precision = 12, scale = 6)
    private BigDecimal costoEstimadoUsd;
}
