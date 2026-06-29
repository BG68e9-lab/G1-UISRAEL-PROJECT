package com.uisrael.drinkhouse.presentacion.dto.response;

public class SecuenciaCodigoResponseDto {

	private Integer negocioId;
	private Integer tipoMovimientoId;
	private Integer ultimoNumero;

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public Integer getTipoMovimientoId() {
		return tipoMovimientoId;
	}

	public void setTipoMovimientoId(Integer tipoMovimientoId) {
		this.tipoMovimientoId = tipoMovimientoId;
	}

	public Integer getUltimoNumero() {
		return ultimoNumero;
	}

	public void setUltimoNumero(Integer ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}

}
