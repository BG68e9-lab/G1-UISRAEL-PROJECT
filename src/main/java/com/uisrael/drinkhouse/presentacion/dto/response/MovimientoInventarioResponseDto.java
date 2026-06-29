package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public class MovimientoInventarioResponseDto {
	
	private Long movimientoId;
	private Integer negocioId;
	private Integer tipoMovimientoId;
	private Long productoId; //
	private Long loteId;
	private String codigoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private UUID usuarioId;
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
