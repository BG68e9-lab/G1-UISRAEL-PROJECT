package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class TipoProducto {

	private Long tipoProductoId;
	private Long categoriaId;
	private Integer negocioId;
	private String nombre;
	private String descripcion;
	private Boolean activo;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;

	public TipoProducto() {
	}

	public TipoProducto(Long tipoProductoId, Long categoriaId, String nombre, String descripcion, Boolean activo) {
		this.tipoProductoId = tipoProductoId;
		this.categoriaId = categoriaId;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.activo = activo;
	}

	public Long getTipoProductoId() {
		return tipoProductoId;
	}

	public void setTipoProductoId(Long tipoProductoId) {
		this.tipoProductoId = tipoProductoId;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
