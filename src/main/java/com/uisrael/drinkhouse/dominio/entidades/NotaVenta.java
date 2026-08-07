package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class NotaVenta {

	private Long notaId;
	private String fecha;
	private String nombreCliente;
	private String productoVendido;
	private String precioUnitario;
	private String total;
	private String observaciones;
	private OffsetDateTime creadoEn;

	public NotaVenta() {
	}

	public NotaVenta(Long notaId, String fecha, String nombreCliente, 
			String productoVendido, String precioUnitario, String total, 
			String observaciones, OffsetDateTime creadoEn) {
		this.notaId = notaId;
		this.fecha = fecha;
		this.nombreCliente = nombreCliente;
		this.productoVendido = productoVendido;
		this.precioUnitario = precioUnitario;
		this.total = total;
		this.observaciones = observaciones;
		this.creadoEn = creadoEn;
	}

	public Long getNotaId() {
		return notaId;
	}

	public void setNotaId(Long notaId) {
		this.notaId = notaId;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getProductoVendido() {
		return productoVendido;
	}

	public void setProductoVendido(String productoVendido) {
		this.productoVendido = productoVendido;
	}

	public String getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(String precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
}
