package com.uisrael.drinkhouse.dominio.entidades;

public class SecuenciaCodigo {

	private Integer negocioId;
	private Integer tipoMovimientoId;
	private Long ultimoNumero;

	public SecuenciaCodigo() {}

	public SecuenciaCodigo(Integer negocioId, Integer tipoMovimientoId, Long ultimoNumero) {
		this.negocioId = negocioId;
		this.tipoMovimientoId = tipoMovimientoId;
		this.ultimoNumero = ultimoNumero;
	}

	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }
	public Integer getTipoMovimientoId() { return tipoMovimientoId; }
	public void setTipoMovimientoId(Integer tipoMovimientoId) { this.tipoMovimientoId = tipoMovimientoId; }
	public Long getUltimoNumero() { return ultimoNumero; }
	public void setUltimoNumero(Long ultimoNumero) { this.ultimoNumero = ultimoNumero; }
}
