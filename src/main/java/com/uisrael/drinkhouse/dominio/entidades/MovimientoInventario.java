package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class MovimientoInventario {

	private Long movimientoId;
	private Long productoId;
	private Long loteId;
	private Long tipoMovimientoId;
	private String tipoMovimientoCodigo;
	private String codigoMovimiento;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private OffsetDateTime creadoEn;
	private Long ventaId;
	private BigDecimal cantidadAnterior;
	private BigDecimal ajuste;
	private BigDecimal cantidadPosterior;

	public MovimientoInventario() {
	}

	public MovimientoInventario(Long movimientoId, Long productoId, Long loteId,
			Long tipoMovimientoId, String tipoMovimientoCodigo, String codigoMovimiento,
			BigDecimal cantidad, BigDecimal precioUnitario, OffsetDateTime creadoEn, Long ventaId) {
		this.movimientoId = movimientoId;
		this.productoId = productoId;
		this.loteId = loteId;
		this.tipoMovimientoId = tipoMovimientoId;
		this.tipoMovimientoCodigo = tipoMovimientoCodigo;
		this.codigoMovimiento = codigoMovimiento;
		this.cantidad = cantidad;
		this.precioUnitario = precioUnitario;
		this.creadoEn = creadoEn;
		this.ventaId = ventaId;
	}

	public Long getMovimientoId() { return movimientoId; }
	public void setMovimientoId(Long movimientoId) { this.movimientoId = movimientoId; }

	public Long getProductoId() { return productoId; }
	public void setProductoId(Long productoId) { this.productoId = productoId; }

	public Long getLoteId() { return loteId; }
	public void setLoteId(Long loteId) { this.loteId = loteId; }

	public Long getTipoMovimientoId() { return tipoMovimientoId; }
	public void setTipoMovimientoId(Long tipoMovimientoId) { this.tipoMovimientoId = tipoMovimientoId; }

	public String getTipoMovimientoCodigo() { return tipoMovimientoCodigo; }
	public void setTipoMovimientoCodigo(String tipoMovimientoCodigo) {
		this.tipoMovimientoCodigo = tipoMovimientoCodigo;
	}

	public String getCodigoMovimiento() { return codigoMovimiento; }
	public void setCodigoMovimiento(String codigoMovimiento) { this.codigoMovimiento = codigoMovimiento; }

	public BigDecimal getCantidad() { return cantidad; }
	public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

	public BigDecimal getPrecioUnitario() { return precioUnitario; }
	public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

	public Long getVentaId() { return ventaId; }
	public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

	public BigDecimal getCantidadAnterior() { return cantidadAnterior; }
	public void setCantidadAnterior(BigDecimal cantidadAnterior) { this.cantidadAnterior = cantidadAnterior; }

	public BigDecimal getAjuste() { return ajuste; }
	public void setAjuste(BigDecimal ajuste) { this.ajuste = ajuste; }

	public BigDecimal getCantidadPosterior() { return cantidadPosterior; }
	public void setCantidadPosterior(BigDecimal cantidadPosterior) { this.cantidadPosterior = cantidadPosterior; }
}
