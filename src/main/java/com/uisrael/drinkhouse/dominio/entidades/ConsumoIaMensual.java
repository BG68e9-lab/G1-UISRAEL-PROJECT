package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class ConsumoIaMensual {
	
	private Long consumoIaMensualId;
	private String mesAnio;
	private Long tokensConsumidos;
	private Double costoEstimado;
	private OffsetDateTime actualizadoEn;
	
	public ConsumoIaMensual() {}

	public ConsumoIaMensual(Long consumoIaMensualId, String mesAnio, Long tokensConsumidos, Double costoEstimado, OffsetDateTime actualizadoEn) {
		this.consumoIaMensualId = consumoIaMensualId;
		this.mesAnio = mesAnio;
		this.tokensConsumidos = tokensConsumidos;
		this.costoEstimado = costoEstimado;
		this.actualizadoEn = actualizadoEn;
	}

	public Long getConsumoIaMensualId() { return consumoIaMensualId; }
	public void setConsumoIaMensualId(Long consumoIaMensualId) { this.consumoIaMensualId = consumoIaMensualId; }
	public String getMesAnio() { return mesAnio; }
	public void setMesAnio(String mesAnio) { this.mesAnio = mesAnio; }
	public Long getTokensConsumidos() { return tokensConsumidos; }
	public void setTokensConsumidos(Long tokensConsumidos) { this.tokensConsumidos = tokensConsumidos; }
	public Double getCostoEstimado() { return costoEstimado; }
	public void setCostoEstimado(Double costoEstimado) { this.costoEstimado = costoEstimado; }
	public OffsetDateTime getActualizadoEn() { return actualizadoEn; }
	public void setActualizadoEn(OffsetDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}