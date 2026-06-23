package com.uisrael.drinkhouse.dominio.entidades;

public class DetalleOrdenCompra {
	
	private Long detalleOrdenCompraId;
	private Long ordenCompraId;
	private Integer cantidad;
	private Double precioUnitario;
	
	public DetalleOrdenCompra() {}

	public DetalleOrdenCompra(Long detalleOrdenCompraId, Long ordenCompraId, Integer cantidad, Double precioUnitario) {
		this.detalleOrdenCompraId = detalleOrdenCompraId;
		this.ordenCompraId = ordenCompraId;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
	}

	public Long getDetalleOrdenCompraId() { return detalleOrdenCompraId; }
	public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) { this.detalleOrdenCompraId = detalleOrdenCompraId; }
	public Long getOrdenCompraId() { return ordenCompraId; }
	public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }
	public Integer getCantidad() { return cantidad; }
	public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
	public Double getPrecioUnitario() { return precioUnitario; }
	public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}