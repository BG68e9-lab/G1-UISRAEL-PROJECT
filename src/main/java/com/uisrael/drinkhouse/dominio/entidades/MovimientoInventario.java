package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;

public class MovimientoInventario {

	private Long movimientoId;
	private String codigoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;

	public MovimientoInventario() {
	}

	public MovimientoInventario(Long movimientoId, String codigoMovimiento,
			BigDecimal cantidad, BigDecimal precioUnitario) {
		this.movimientoId = movimientoId;
		this.codigoMovimiento = codigoMovimiento;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
	}

	public Long getMovimientoId() { return movimientoId; }
	public void setMovimientoId(Long movimientoId) { this.movimientoId = movimientoId; }

	public String getCodigoMovimiento() { return codigoMovimiento; }
	public void setCodigoMovimiento(String codigoMovimiento) { this.codigoMovimiento = codigoMovimiento; }

	public BigDecimal getCantidad() { return cantidad; }
	public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

	public BigDecimal getPrecioUnitario() { return precioUnitario; }
	public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }
}
