package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "logs_auditoria")
public class LogAuditoriaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id", updatable = false, nullable = false)
	private Long logId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private UsuarioEntity fkUsuarioEntity;

	@Column(name = "entidad", nullable = false, length = 50)
	private String entidad;

	@Column(name = "entidad_id", nullable = false, length = 100)
	private String entidadId;

	@Column(name = "accion", nullable = false, length = 30)
	private String accion;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "detalle", columnDefinition = "jsonb")
	private String detalle;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}
