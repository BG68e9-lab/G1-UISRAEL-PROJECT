package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class IdentificacionIa {
	
	private Long identificacionIaId;
	private String nombreModelo;
	private Double probabilidad;
	private String resultado;
	private OffsetDateTime creadoEn;
	
	public IdentificacionIa() {}

	public IdentificacionIa(Long identificacionIaId, String nombreModelo, Double probabilidad, String resultado, OffsetDateTime creadoEn) {
		this.identificacionIaId = identificacionIaId;
		this.nombreModelo = nombreModelo;
		this.probabilidad = probabilidad;
		this.resultado = resultado;
		this.creadoEn = creadoEn;
	}

	public Long getIdentificacionIaId() { return identificacionIaId; }
	public void setIdentificacionIaId(Long identificacionIaId) { this.identificacionIaId = identificacionIaId; }
	public String getNombreModelo() { return nombreModelo; }
	public void setNombreModelo(String nombreModelo) { this.nombreModelo = nombreModelo; }
	public Double getProbabilidad() { return probabilidad; }
	public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }
	public String getResultado() { return resultado; }
	public void setResultado(String resultado) { this.resultado = resultado; }
	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}