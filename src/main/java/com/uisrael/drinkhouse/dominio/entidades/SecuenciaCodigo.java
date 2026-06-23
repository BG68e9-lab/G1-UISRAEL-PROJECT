package com.uisrael.drinkhouse.dominio.entidades;

public class SecuenciaCodigo {

	private Integer negocioId;
	private Integer tipoMovimientoId;
	private Integer ultimoNumero;

	public SecuenciaCodigo(Integer negocioId, Integer tipoMovimientoId, Integer ultimoNumero) {

		this.negocioId = negocioId;
		this.tipoMovimientoId = tipoMovimientoId;
		this.ultimoNumero = ultimoNumero;
	}

	public SecuenciaCodigo() {

	}

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
