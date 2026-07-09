package com.uisrael.drinkhouse.dominio.entidades;

public class SecuenciaCodigo {

	private Long ultimoNumero;

	public SecuenciaCodigo() {
	}

	public SecuenciaCodigo(Long ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}

	public Long getUltimoNumero() {
		return ultimoNumero;
	}

	public void setUltimoNumero(Long ultimoNumero) {
		this.ultimoNumero = ultimoNumero;
	}
}
