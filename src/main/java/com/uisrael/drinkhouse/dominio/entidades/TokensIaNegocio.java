package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class TokensIaNegocio {
	
	private Long tokenIaNegocioId;
	private Integer negocioId;
	private String claveConfiguracion;
	private String valorToken;
	private OffsetDateTime registradoEn;
	
	public TokensIaNegocio() {}

	public TokensIaNegocio(Long tokenIaNegocioId, Integer negocioId, String claveConfiguracion, String valorToken, OffsetDateTime registradoEn) {
		this.tokenIaNegocioId = tokenIaNegocioId;
		this.negocioId = negocioId;
		this.claveConfiguracion = claveConfiguracion;
		this.valorToken = valorToken;
		this.registradoEn = registradoEn;
	}

	public Long getTokenIaNegocioId() { return tokenIaNegocioId; }
	public void setTokenIaNegocioId(Long tokenIaNegocioId) { this.tokenIaNegocioId = tokenIaNegocioId; }
	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }
	public String getClaveConfiguracion() { return claveConfiguracion; }
	public void setClaveConfiguracion(String claveConfiguracion) { this.claveConfiguracion = claveConfiguracion; }
	public String getValorToken() { return valorToken; }
	public void setValorToken(String valorToken) { this.valorToken = valorToken; }
	public OffsetDateTime getRegistradoEn() { return registradoEn; }
	public void setRegistradoEn(OffsetDateTime registradoEn) { this.registradoEn = registradoEn; }
}