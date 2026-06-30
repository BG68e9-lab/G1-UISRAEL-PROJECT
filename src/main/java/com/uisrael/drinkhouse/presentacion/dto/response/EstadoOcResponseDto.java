package com.uisrael.drinkhouse.presentacion.dto.response;

public class EstadoOcResponseDto {

	private Integer estadoOcId;
	private String codigo;
	private String etiqueta;

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