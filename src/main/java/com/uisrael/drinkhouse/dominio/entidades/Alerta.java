package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class Alerta {

	private Long alertaId;
	private Integer negocioId;
	private String tipoAlerta;
	private String referenciaTipo;
	private Long referenciaId;
	private String mensaje;
	private Boolean atendida;
	private OffsetDateTime creadoEn;

	public Alerta() {}

	public Alerta(Long alertaId, Integer negocioId, String tipoAlerta, String referenciaTipo,
			Long referenciaId, String mensaje, Boolean atendida, OffsetDateTime creadoEn) {
		this.alertaId = alertaId;
		this.negocioId = negocioId;
		this.tipoAlerta = tipoAlerta;
		this.referenciaTipo = referenciaTipo;
		this.referenciaId = referenciaId;
		this.mensaje = mensaje;
		this.atendida = atendida;
		this.creadoEn = creadoEn;
	}

	public Long getAlertaId() { return alertaId; }
	public void setAlertaId(Long alertaId) { this.alertaId = alertaId; }
	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }
	public String getTipoAlerta() { return tipoAlerta; }
	public void setTipoAlerta(String tipoAlerta) { this.tipoAlerta = tipoAlerta; }
	public String getReferenciaTipo() { return referenciaTipo; }
	public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }
	public Long getReferenciaId() { return referenciaId; }
	public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }
	public String getMensaje() { return mensaje; }
	public void setMensaje(String mensaje) { this.mensaje = mensaje; }
	public Boolean getAtendida() { return atendida; }
	public void setAtendida(Boolean atendida) { this.atendida = atendida; }
	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}
