package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class Alerta {
	
	private Long alertaId;
	private String tipo;
	private String mensaje;
	private Boolean leido;
	private OffsetDateTime creadoEn;
	
	public Alerta() {}

	public Alerta(Long alertaId, String tipo, String mensaje, Boolean leido, OffsetDateTime creadoEn) {
		this.alertaId = alertaId;
		this.tipo = tipo;
		this.mensaje = mensaje;
		this.leido = leido;
		this.creadoEn = creadoEn;
	}

	public Long getAlertaId() { return alertaId; }
	public void setAlertaId(Long alertaId) { this.alertaId = alertaId; }
	public String getTipo() { return tipo; }
	public void setTipo(String tipo) { this.tipo = tipo; }
	public String getMensaje() { return mensaje; }
	public void setMensaje(String mensaje) { this.mensaje = mensaje; }
	public Boolean getLeido() { return leido; }
	public void setLeido(Boolean leido) { this.leido = leido; }
	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}