package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class HistorialPrecioProducto {

	private Long historialPrecioId;
	private Long productoId;
	private BigDecimal costoPromedioAnterior;
	private BigDecimal margenGananciaAnterior;
	private BigDecimal precioVentaAnterior;
	private BigDecimal costoPromedioNuevo;
	private BigDecimal margenGananciaNuevo;
	private BigDecimal precioVentaNuevo;
	private String motivo;
	private String usuarioModificador;
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String facturaRelacionada;
	private Long ordenCompraId;
	private String sessionId;
	private String direccionIp;

	public HistorialPrecioProducto() {
	}

	public Long getHistorialPrecioId() {
		return historialPrecioId;
	}

	public void setHistorialPrecioId(Long historialPrecioId) {
		this.historialPrecioId = historialPrecioId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public BigDecimal getCostoPromedioAnterior() {
		return costoPromedioAnterior;
	}

	public void setCostoPromedioAnterior(BigDecimal costoPromedioAnterior) {
		this.costoPromedioAnterior = costoPromedioAnterior;
	}

	public BigDecimal getMargenGananciaAnterior() {
		return margenGananciaAnterior;
	}

	public void setMargenGananciaAnterior(BigDecimal margenGananciaAnterior) {
		this.margenGananciaAnterior = margenGananciaAnterior;
	}

	public BigDecimal getPrecioVentaAnterior() {
		return precioVentaAnterior;
	}

	public void setPrecioVentaAnterior(BigDecimal precioVentaAnterior) {
		this.precioVentaAnterior = precioVentaAnterior;
	}

	public BigDecimal getCostoPromedioNuevo() {
		return costoPromedioNuevo;
	}

	public void setCostoPromedioNuevo(BigDecimal costoPromedioNuevo) {
		this.costoPromedioNuevo = costoPromedioNuevo;
	}

	public BigDecimal getMargenGananciaNuevo() {
		return margenGananciaNuevo;
	}

	public void setMargenGananciaNuevo(BigDecimal margenGananciaNuevo) {
		this.margenGananciaNuevo = margenGananciaNuevo;
	}

	public BigDecimal getPrecioVentaNuevo() {
		return precioVentaNuevo;
	}

	public void setPrecioVentaNuevo(BigDecimal precioVentaNuevo) {
		this.precioVentaNuevo = precioVentaNuevo;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getUsuarioModificador() {
		return usuarioModificador;
	}

	public void setUsuarioModificador(String usuarioModificador) {
		this.usuarioModificador = usuarioModificador;
	}

	public OffsetDateTime getFechaCambio() {
		return fechaCambio;
	}

	public void setFechaCambio(OffsetDateTime fechaCambio) {
		this.fechaCambio = fechaCambio;
	}

	public String getOrigenCambio() {
		return origenCambio;
	}

	public void setOrigenCambio(String origenCambio) {
		this.origenCambio = origenCambio;
	}

	public String getFacturaRelacionada() {
		return facturaRelacionada;
	}

	public void setFacturaRelacionada(String facturaRelacionada) {
		this.facturaRelacionada = facturaRelacionada;
	}

	public Long getOrdenCompraId() {
		return ordenCompraId;
	}

	public void setOrdenCompraId(Long ordenCompraId) {
		this.ordenCompraId = ordenCompraId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getDireccionIp() {
		return direccionIp;
	}

	public void setDireccionIp(String direccionIp) {
		this.direccionIp = direccionIp;
	}
}
