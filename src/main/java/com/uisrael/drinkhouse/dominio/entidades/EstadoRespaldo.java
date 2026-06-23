package com.uisrael.drinkhouse.dominio.entidades;

public class EstadoRespaldo {

	private Integer estadoRespaldoId;
	private String codigo;
	private String etiqueta;
	private String icono;

	public EstadoRespaldo(Integer estadoRespaldoId, String codigo, String etiqueta, String icono) {

		this.estadoRespaldoId = estadoRespaldoId;
		this.codigo = codigo;
		this.etiqueta = etiqueta;
		this.icono = icono;
	}

	public EstadoRespaldo() {
	}

	public Integer getEstadoRespaldoId() {
		return estadoRespaldoId;
	}

	public void setEstadoRespaldoId(Integer estadoRespaldoId) {
		this.estadoRespaldoId = estadoRespaldoId;
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

	public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}

}
