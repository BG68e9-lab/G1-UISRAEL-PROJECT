package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class HistorialIvaProducto {

	private Long historialIvaId;
	private Long productoId;
	private BigDecimal tarifaIvaAnterior;
	private String codigoPorcentajeAnterior;
	private String descripcionAnterior;
	private BigDecimal tarifaIvaNueva;
	private String codigoPorcentajeNuevo;
	private String descripcionNueva;
	private String motivo;
	private String usuarioModificador;
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String resolucionSri;
	private LocalDate fechaVigencia;
	private String sessionId;
	private String direccionIp;

	public HistorialIvaProducto() {
	}

	public Long getHistorialIvaId() {
		return historialIvaId;
	}

	public void setHistorialIvaId(Long historialIvaId) {
		this.historialIvaId = historialIvaId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public BigDecimal getTarifaIvaAnterior() {
		return tarifaIvaAnterior;
	}

	public void setTarifaIvaAnterior(BigDecimal tarifaIvaAnterior) {
		this.tarifaIvaAnterior = tarifaIvaAnterior;
	}

	public String getCodigoPorcentajeAnterior() {
		return codigoPorcentajeAnterior;
	}

	public void setCodigoPorcentajeAnterior(String codigoPorcentajeAnterior) {
		this.codigoPorcentajeAnterior = codigoPorcentajeAnterior;
	}

	public String getDescripcionAnterior() {
		return descripcionAnterior;
	}

	public void setDescripcionAnterior(String descripcionAnterior) {
		this.descripcionAnterior = descripcionAnterior;
	}

	public BigDecimal getTarifaIvaNueva() {
		return tarifaIvaNueva;
	}

	public void setTarifaIvaNueva(BigDecimal tarifaIvaNueva) {
		this.tarifaIvaNueva = tarifaIvaNueva;
	}

	public String getCodigoPorcentajeNuevo() {
		return codigoPorcentajeNuevo;
	}

	public void setCodigoPorcentajeNuevo(String codigoPorcentajeNuevo) {
		this.codigoPorcentajeNuevo = codigoPorcentajeNuevo;
	}

	public String getDescripcionNueva() {
		return descripcionNueva;
	}

	public void setDescripcionNueva(String descripcionNueva) {
		this.descripcionNueva = descripcionNueva;
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

	public String getResolucionSri() {
		return resolucionSri;
	}

	public void setResolucionSri(String resolucionSri) {
		this.resolucionSri = resolucionSri;
	}

	public LocalDate getFechaVigencia() {
		return fechaVigencia;
	}

	public void setFechaVigencia(LocalDate fechaVigencia) {
		this.fechaVigencia = fechaVigencia;
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
