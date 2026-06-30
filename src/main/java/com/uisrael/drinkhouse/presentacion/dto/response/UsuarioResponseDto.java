package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

public class UsuarioResponseDto {

	private UUID usuarioId;
	private Integer negocioId;
	private Integer rolId;
	private String nombres;
	private String apellidos;
	private String email;
	private String proveedorSso;
	private String estadoCuenta;
	private OffsetDateTime activadoEn;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;

	public UUID getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(UUID usuarioId) {
		this.usuarioId = usuarioId;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public Integer getRolId() {
		return rolId;
	}

	public void setRolId(Integer rolId) {
		this.rolId = rolId;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProveedorSso() {
		return proveedorSso;
	}

	public void setProveedorSso(String proveedorSso) {
		this.proveedorSso = proveedorSso;
	}

	public String getEstadoCuenta() {
		return estadoCuenta;
	}

	public void setEstadoCuenta(String estadoCuenta) {
		this.estadoCuenta = estadoCuenta;
	}

	public OffsetDateTime getActivadoEn() {
		return activadoEn;
	}

	public void setActivadoEn(OffsetDateTime activadoEn) {
		this.activadoEn = activadoEn;
	}

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}

	public OffsetDateTime getActualizadoEn() {
		return actualizadoEn;
	}

	public void setActualizadoEn(OffsetDateTime actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}
}