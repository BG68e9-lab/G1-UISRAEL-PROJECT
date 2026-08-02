package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "producto_ice_historico")
public class ProductoIceEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private ProductoEntity fkProductoEntity;

	@Column(name = "tipo_ice", nullable = false, length = 20)
	private String tipoIce;

	@Column(name = "valor", nullable = false, precision = 12, scale = 4)
	private BigDecimal valor;

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
