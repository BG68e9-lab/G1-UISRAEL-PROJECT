package com.uisrael.drinkhouse.presentacion.dto.response;

public class TipoMovimientoResponseDto {

	private Integer tipoMovimientoId;
	private String codigo;
	private String prefijoCodigo;
	private String descripcion;
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
