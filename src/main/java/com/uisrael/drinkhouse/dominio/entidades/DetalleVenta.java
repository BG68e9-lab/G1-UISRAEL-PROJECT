package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;

public class DetalleVenta {

	private Long detalleVentaId;
	private Long ventaId;
	private Long productoId;
	private String productoNombre;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal subtotal;

	public DetalleVenta() {
	}

	public Long getDetalleVentaId() { return detalleVentaId; }
	public void setDetalleVentaId(Long detalleVentaId) { this.detalleVentaId = detalleVentaId; }

	public Long getVentaId() { return ventaId; }
	public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

	public Long getProductoId() { return productoId; }
	public void setProductoId(Long productoId) { this.productoId = productoId; }

	public String getProductoNombre() { return productoNombre; }
	public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

	public Integer getCantidad() { return cantidad; }
	public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

	public BigDecimal getPrecioUnitario() { return precioUnitario; }
	public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
}
