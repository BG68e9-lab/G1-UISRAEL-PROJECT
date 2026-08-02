package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tasas_iva")
public class TasaIvaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tasa_iva_id")
	private Long tasaIvaId;

	@Column(name = "porcentaje", nullable = false, precision = 5, scale = 2)
	private BigDecimal porcentaje;

	@Column(name = "vigente_desde", nullable = false)
	private OffsetDateTime vigenteDesde;

	@Column(name = "vigente_hasta")
	private OffsetDateTime vigenteHasta;

	@Column(name = "motivo", length = 255)
	private String motivo;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}
