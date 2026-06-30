package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;

public class CategoriaResponseDto {

	private Integer categoriaId;
	private Integer negocioId;
	private String nombre;
	private BigDecimal margenGananciaPct;
	private Boolean activo;

	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public BigDecimal getMargenGananciaPct() {
		return margenGananciaPct;
	}

	public void setMargenGananciaPct(BigDecimal margenGananciaPct) {
		this.margenGananciaPct = margenGananciaPct;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}