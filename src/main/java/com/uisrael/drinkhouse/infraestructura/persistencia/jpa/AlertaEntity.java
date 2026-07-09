package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

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
@Table(name = "alertas")
public class AlertaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alerta_id")
	private Long alertaId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@Column(name = "tipo_alerta", nullable = false, length = 40)
	private String tipoAlerta;

	@Column(name = "referencia_tipo", nullable = false, length = 30)
	private String referenciaTipo;

	@Column(name = "referencia_id")
	private Long referenciaId;

	@Column(name = "mensaje", nullable = false, length = 255)
	private String mensaje;

	@Column(name = "atendida", nullable = false)
	private Boolean atendida;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@Column(name = "atendida_en")
	private OffsetDateTime atendidaEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
		if (this.atendida == null) this.atendida = false;
	}
}
