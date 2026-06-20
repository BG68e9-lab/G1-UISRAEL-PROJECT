package com.uisrael.drinkhouse.dominio.entidades;

public class EstadoOc {
	
	private Integer estadoOcId;
	private String codigo;
	private String etiqueta;
	
	public EstadoOc() {

	}

	public EstadoOc(Integer estadoOcId, String codigo, String etiqueta) {

		this.estadoOcId = estadoOcId;
		this.codigo = codigo;
		this.etiqueta = etiqueta;
	}
	
	public Integer getEstadoOcId() {
		return estadoOcId;
	}
	public void setEstadoOcId(Integer estadoOcId) {
		this.estadoOcId = estadoOcId;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}


}
