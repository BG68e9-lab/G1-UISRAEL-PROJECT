package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NegocioResponseDto {

	private Integer negocioId;
	private String nombre;
	private String ruc;
	private Boolean activo;
	private LocalDateTime creadoEn;
	
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
	public LocalDateTime getCreadoEn() {
		return creadoEn;
	}
	public void setCreadoEn(LocalDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
	
	
}