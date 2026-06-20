package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "codigos_acceso")
public class CodigoAccesoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "codigo_acceso_id")
	private UUID codigoAccesoId;
	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private UsuarioEntity usuarioId;
	@Column(name = "tipo_codigo", nullable = false, length = 20)
	private String tipoCodigo;
	@Column(name = "codigo_hash", nullable = false, length = 255)
	private String codigoHash;
	@Column(name = "expira_en", updatable = false)
	private OffsetDateTime expiraEn;
	@Column(name = "usado")
	private Boolean usado;
	@Column(name = "usado_en")
	private OffsetDateTime usadoEn;
	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}

}
