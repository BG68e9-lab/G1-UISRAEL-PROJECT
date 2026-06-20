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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class UsuarioEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "usuario_id", updatable = false, nullable = false)
	private UUID usuarioId;
	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;
	@ManyToOne
	@JoinColumn(name = "rol_id", nullable = false)
	private RolEntity rolId;
	@Column(name = "nombres", nullable = false, length = 80)
	private String nombres;
	@Column(name = "apellidos", nullable = false, length = 80)
	private String apellidos;
	@Column(name = "email", nullable = false, length = 150)
	private String email;
	@Column(name = "password_hash", length = 255)
	private String passwordHash;
	@Column(name = "proveedor_sso", length = 20)
	private String proveedorSso;
	@Column(name = "sso_subject_id", length = 255)
	private String ssoSubjectId;
	@Column(name = "estado_cuenta", length = 20)
	private String estadoCuenta;
	@Column(name = "activado_en")
	private OffsetDateTime activadoEn;
	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;
	@Column(name = "actualizado_en")
	private OffsetDateTime actualizadoEn;
	
	
	@PrePersist
	protected void onCreate() {
	    this.creadoEn = OffsetDateTime.now();
	    this.actualizadoEn = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
	    this.actualizadoEn = OffsetDateTime.now();
	}

}
