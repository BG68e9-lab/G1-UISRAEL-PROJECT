package com.uisrael.drinkhouse.dominio.entidades;

public class Proveedor {

	private Integer proveedorId;
	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	private String email;

	public Proveedor() {
	}

	public Proveedor(Integer proveedorId, String ruc, String razonSocial, String direccion,
			String telefono, String email) {
		this.proveedorId = proveedorId;
		this.ruc = ruc;
		this.razonSocial = razonSocial;
		this.direccion = direccion;
		this.telefono = telefono;
		this.email = email;
	}

	public Integer getProveedorId() { return proveedorId; }
	public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

	public String getRuc() { return ruc; }
	public void setRuc(String ruc) { this.ruc = ruc; }

	public String getRazonSocial() { return razonSocial; }
	public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

	public String getDireccion() { return direccion; }
	public void setDireccion(String direccion) { this.direccion = direccion; }

	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
}
