package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Snapshot historico y consolidado del precio de un producto: costo, margen,
 * precio de venta, IVA e ICE aplicados en ese momento, y el precio final con
 * impuestos ya calculado. Se genera un registro nuevo cada vez que cambia
 * cualquiera de estos valores (creacion del producto, edicion de
 * costo/margen/precio, o cambio de la tasa de IVA/ICE que le aplica), y se
 * cierra (vigenteHasta) el registro anterior. Asi queda el historico
 * consolidado a traves del tiempo que se necesitaba.
 */
public class ProductoPrecioHistorico {

	private Long id;
	private Long productoId;
	private BigDecimal costoPromedio;
	private BigDecimal margenGanancia;
	private BigDecimal precioVenta;
	private BigDecimal ivaPorcentajeAplicado;
	private String iceTipoAplicado;
	private BigDecimal iceValorAplicado;
	private BigDecimal precioFinalConImpuestos;
	private OffsetDateTime vigenteDesde;
	private OffsetDateTime vigenteHasta;
	private String motivo;
	private OffsetDateTime creadoEn;

	public ProductoPrecioHistorico() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public BigDecimal getCostoPromedio() {
		return costoPromedio;
	}

	public void setCostoPromedio(BigDecimal costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public BigDecimal getMargenGanancia() {
		return margenGanancia;
	}

	public void setMargenGanancia(BigDecimal margenGanancia) {
		this.margenGanancia = margenGanancia;
	}

	public BigDecimal getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(BigDecimal precioVenta) {
		this.precioVenta = precioVenta;
	}

	public BigDecimal getIvaPorcentajeAplicado() {
		return ivaPorcentajeAplicado;
	}

	public void setIvaPorcentajeAplicado(BigDecimal ivaPorcentajeAplicado) {
		this.ivaPorcentajeAplicado = ivaPorcentajeAplicado;
	}

	public String getIceTipoAplicado() {
		return iceTipoAplicado;
	}

	public void setIceTipoAplicado(String iceTipoAplicado) {
		this.iceTipoAplicado = iceTipoAplicado;
	}

	public BigDecimal getIceValorAplicado() {
		return iceValorAplicado;
	}

	public void setIceValorAplicado(BigDecimal iceValorAplicado) {
		this.iceValorAplicado = iceValorAplicado;
	}

	public BigDecimal getPrecioFinalConImpuestos() {
		return precioFinalConImpuestos;
	}

	public void setPrecioFinalConImpuestos(BigDecimal precioFinalConImpuestos) {
		this.precioFinalConImpuestos = precioFinalConImpuestos;
	}

	public OffsetDateTime getVigenteDesde() {
		return vigenteDesde;
	}

	public void setVigenteDesde(OffsetDateTime vigenteDesde) {
		this.vigenteDesde = vigenteDesde;
	}

	public OffsetDateTime getVigenteHasta() {
		return vigenteHasta;
	}

	public void setVigenteHasta(OffsetDateTime vigenteHasta) {
		this.vigenteHasta = vigenteHasta;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public OffsetDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(OffsetDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
}
