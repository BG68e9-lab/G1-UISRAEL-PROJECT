package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Usuario {

	private UUID usuarioId;
	private String nombres;
	private String apellidos;
	private String email;
	private String passwordHash;
	private String proveedorSso;
	private String ssoSubjectId;
	private String estadoCuenta;
	private OffsetDateTime activadoEn;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;

	public Usuario() {
	}

	public Usuario(UUID usuarioId, String nombres, String apellidos, String email,
			String passwordHash, String proveedorSso, String ssoSubjectId, String estadoCuenta,
			OffsetDateTime activadoEn, OffsetDateTime creadoEn, OffsetDateTime actualizadoEn) {
		this.usuarioId = usuarioId;
		this.nombres = nombres;
		this.apellidos = apellidos;
		this.email = email;
		this.passwordHash = passwordHash;
		this.proveedorSso = proveedorSso;
		this.ssoSubjectId = ssoSubjectId;
		this.estadoCuenta = estadoCuenta;
		this.activadoEn = activadoEn;
		this.creadoEn = creadoEn;
		this.actualizadoEn = actualizadoEn;
	}

	public UUID getUsuarioId() { return usuarioId; }
	public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

	public String getNombres() { return nombres; }
	public void setNombres(String nombres) { this.nombres = nombres; }

	public String getApellidos() { return apellidos; }
	public void setApellidos(String apellidos) { this.apellidos = apellidos; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getPasswordHash() { return passwordHash; }
	public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

	public String getProveedorSso() { return proveedorSso; }
	public void setProveedorSso(String proveedorSso) { this.proveedorSso = proveedorSso; }

	public String getSsoSubjectId() { return ssoSubjectId; }
	public void setSsoSubjectId(String ssoSubjectId) { this.ssoSubjectId = ssoSubjectId; }

	public String getEstadoCuenta() { return estadoCuenta; }
	public void setEstadoCuenta(String estadoCuenta) { this.estadoCuenta = estadoCuenta; }

	public OffsetDateTime getActivadoEn() { return activadoEn; }
	public void setActivadoEn(OffsetDateTime activadoEn) { this.activadoEn = activadoEn; }

	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

	public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
	public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}
