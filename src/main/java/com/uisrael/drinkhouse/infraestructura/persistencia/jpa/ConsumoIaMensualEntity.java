package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "consumo_ia_mensual")
public class ConsumoIaMensualEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consumo_ia_id")
	private Long consumoIaId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@Column(name = "periodo", nullable = false)
	private LocalDate periodo;

	@Column(name = "total_tokens_input", nullable = false)
	private Long totalTokensInput;

	@Column(name = "total_tokens_output", nullable = false)
	private Long totalTokensOutput;

	@Column(name = "costo_estimado_usd", nullable = false, precision = 12, scale = 4)
	private BigDecimal costoEstimadoUsd;

	@Column(name = "cerrado_en")
	private OffsetDateTime cerradoEn;
}
