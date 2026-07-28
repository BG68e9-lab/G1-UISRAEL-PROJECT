package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DetalleOrdenCompra {

	private Long detalleOcId;
	private Long ordenCompraId;
	private Long productoId;
	private String descripcionFactura;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal subtotal;
	private LocalDate fechaVencimiento;

	public DetalleOrdenCompra() {}

	public DetalleOrdenCompra(Long detalleOcId, Long ordenCompraId, Long productoId,
			String descripcionFactura, BigDecimal cantidad, BigDecimal precioUnitario,
			BigDecimal subtotal, LocalDate fechaVencimiento) {
		this.detalleOcId = detalleOcId;
		this.ordenCompraId = ordenCompraId;
		this.productoId = productoId;
		this.descripcionFactura = descripcionFactura;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.subtotal = subtotal;
		this.fechaVencimiento = fechaVencimiento;
	}

	public Long getDetalleOcId() { return detalleOcId; }
	public void setDetalleOcId(Long detalleOcId) { this.detalleOcId = detalleOcId; }
	public Long getOrdenCompraId() { return ordenCompraId; }
	public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }
	public Long getProductoId() { return productoId; }
	public void setProductoId(Long productoId) { this.productoId = productoId; }
	public String getDescripcionFactura() { return descripcionFactura; }
	public void setDescripcionFactura(String descripcionFactura) { this.descripcionFactura = descripcionFactura; }
	public BigDecimal getCantidad() { return cantidad; }
	public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }
	public BigDecimal getPrecioUnitario() { return precioUnitario; }
	public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
	public LocalDate getFechaVencimiento() { return fechaVencimiento; }
	public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
