package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;
import java.util.UUID;

public class LogAuditoria {
	
	private Long logId;
	private Integer negocioId;
	private UUID usuarioId;
	private String entidad;
	private String entidadId;
	private String accion;
	private String detalle;
	private OffsetDateTime creadoEn;
	
	public LogAuditoria() {

	}

	public LogAuditoria(Long logId, Integer negocioId, UUID usuarioId, String entidad, String entidadId, String accion,
			String detalle, OffsetDateTime creadoEn) {

		this.logId = logId;
		this.negocioId = negocioId;
		this.usuarioId = usuarioId;
		this.entidad = entidad;
		this.entidadId = entidadId;
		this.accion = accion;
		this.detalle = detalle;
		this.creadoEn = creadoEn;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public UUID getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(UUID usuarioId) {
		this.usuarioId = usuarioId;
	}

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getEntidadId() {
		return entidadId;
	}

	public void setEntidadId(String entidadId) {
		this.entidadId = entidadId;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
	
}
