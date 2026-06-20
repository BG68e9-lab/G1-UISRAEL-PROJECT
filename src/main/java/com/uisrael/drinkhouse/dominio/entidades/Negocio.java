package com.uisrael.drinkhouse.dominio.entidades;

import java.time.LocalDateTime;

public class Negocio {
	
	private Integer negocioId;
	private String nombre;
	private String ruc;
	private Boolean activo;
	private LocalDateTime creadoEn;
	
	
	public Negocio() {

	}


	public Negocio(Integer negocioId, String nombre, String ruc, Boolean activo, LocalDateTime creandoEn) {

		this.negocioId = negocioId;
		this.nombre = nombre;
		this.ruc = ruc;
		this.activo = activo;
		this.creadoEn = creandoEn;
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


	public String getRuc() {
		return ruc;
	}


	public void setRuc(String ruc) {
		this.ruc = ruc;
	}


	public Boolean getActivo() {
		return activo;
	}


	public void setActivo(Boolean activo) {
		this.activo = activo;
	}


	public LocalDateTime getCreandoEn() {
		return creadoEn;
	}


	public void setCreandoEn(LocalDateTime creandoEn) {
		this.creadoEn = creandoEn;
	}
	

}
