package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.util.UUID;


public class MovimientosInventario {

	private Long movimientoId;
	private Integer negocioId;
	private Integer tipoMovimientoId;
	private Long productoId; //
	private Long loteId;
	private String codigoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private UUID usuarioId;
	public MovimientosInventario(Long movimientoId, Integer negocioId, Integer tipoMovimientoId, Long productoId,
			Long loteId, String codigoMovimiento, BigDecimal cantidad, BigDecimal precioUnitario, UUID usuarioId) {
		super();
		this.movimientoId = movimientoId;
		this.negocioId = negocioId;
		this.tipoMovimientoId = tipoMovimientoId;
		this.productoId = productoId;
		this.loteId = loteId;
		this.codigoMovimiento = codigoMovimiento;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.usuarioId = usuarioId;
	}
	public MovimientosInventario() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Long getMovimientoId() {
		return movimientoId;
	}
	public void setMovimientoId(Long movimientoId) {
		this.movimientoId = movimientoId;
	}
	public Integer getNegocioId() {
		return negocioId;
	}
	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}
	public Integer getTipoMovimientoId() {
		return tipoMovimientoId;
	}
	public void setTipoMovimientoId(Integer tipoMovimientoId) {
		this.tipoMovimientoId = tipoMovimientoId;
	}
	public Long getProductoId() {
		return productoId;
	}
	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}
	public Long getLoteId() {
		return loteId;
	}
	public void setLoteId(Long loteId) {
		this.loteId = loteId;
	}
	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}
	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}
	public BigDecimal getCantidad() {
		return cantidad;
	}
	public void setCantidad(BigDecimal cantidad) {
		this.cantidad = cantidad;
	}
	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}
	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}
	public UUID getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(UUID usuarioId) {
		this.usuarioId = usuarioId;
	}
	
	
	
	
	
}
