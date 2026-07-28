package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;

public class DetalleOrdenCompra {

	private Long detalleOrdenCompraId;
	private Long ordenCompraId;
	private Long productoId;
	private String productoNombre;
	private String productoMarca;
	private String productoTipo;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal subtotal;
	private String observaciones;

	public DetalleOrdenCompra() {
	}

	public void calcularSubtotal() {
		if (cantidad != null && precioUnitario != null) {
			this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
		} else {
			this.subtotal = BigDecimal.ZERO;
		}
	}

	public Long getDetalleOrdenCompraId() {
		return detalleOrdenCompraId;
	}

	public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) {
		this.detalleOrdenCompraId = detalleOrdenCompraId;
	}

	public Long getOrdenCompraId() {
		return ordenCompraId;
	}

	public void setOrdenCompraId(Long ordenCompraId) {
		this.ordenCompraId = ordenCompraId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public String getProductoNombre() {
		return productoNombre;
	}

	public void setProductoNombre(String productoNombre) {
		this.productoNombre = productoNombre;
	}

	public String getProductoMarca() {
		return productoMarca;
	}

	public void setProductoMarca(String productoMarca) {
		this.productoMarca = productoMarca;
	}

	public String getProductoTipo() {
		return productoTipo;
	}

	public void setProductoTipo(String productoTipo) {
		this.productoTipo = productoTipo;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
}
