package com.uisrael.drinkhouse.presentacion.dto.response;

public class ProveedorResponseDto {
	private Integer proveedorId;
	private Integer negocioId;
	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	private String email;
	public Integer getProveedorId() {
		return proveedorId;
	}
	public void setProveedorId(Integer proveedorId) {
		this.proveedorId = proveedorId;
	}
	public Integer getNegocioId() {
		return negocioId;
	}
	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}
	public String getRuc() {
		return ruc;
	}
	public void setRuc(String ruc) {
		this.ruc = ruc;
	}
	public String getRazonSocial() {
		return razonSocial;
	}
	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
