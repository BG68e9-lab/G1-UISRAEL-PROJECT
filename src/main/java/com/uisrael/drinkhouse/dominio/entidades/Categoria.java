package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class Categoria {

	private Long categoriaId;
	private Integer negocioId;
	private String nombre;
	private BigDecimal margenGananciaPct;
	private Boolean activo;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;

	public Categoria() {
	}

	public Categoria(Long categoriaId, String nombre, BigDecimal margenGananciaPct, Boolean activo) {
		this.categoriaId = categoriaId;
		this.nombre = nombre;
		this.margenGananciaPct = margenGananciaPct;
		this.activo = activo;
	}

	public Long getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Long categoriaId) {
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

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}

	public OffsetDateTime getActualizadoEn() {
		return actualizadoEn;
	}

	public void setActualizadoEn(OffsetDateTime actualizadoEn) {
		this.actualizadoEn = actualizadoEn;
	}
}
