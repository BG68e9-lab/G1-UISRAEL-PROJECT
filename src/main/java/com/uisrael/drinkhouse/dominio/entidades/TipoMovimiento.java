package com.uisrael.drinkhouse.dominio.entidades;

public class TipoMovimiento {

	private Integer tipoMovimientoId;
	private String codigo;
	private String prefijoCodigo;
	private String descripcion;

	public TipoMovimiento(Integer tipoMovimientoId, String codigo, String prefijoCodigo, String descripcion) {

		this.tipoMovimientoId = tipoMovimientoId;
		this.codigo = codigo;
		this.prefijoCodigo = prefijoCodigo;
		this.descripcion = descripcion;
	}

	public TipoMovimiento() {
	}

	public Integer getTipoMovimientoId() {
		return tipoMovimientoId;
	}

	public void setTipoMovimientoId(Integer tipoMovimientoId) {
		this.tipoMovimientoId = tipoMovimientoId;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getPrefijoCodigo() {
		return prefijoCodigo;
	}

	public void setPrefijoCodigo(String prefijoCodigo) {
		this.prefijoCodigo = prefijoCodigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
