package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Entidad de dominio que representa un registro histórico de cambio de ICE (Impuesto a Consumos Especiales) de un producto.
 * Captura cambios en aplicabilidad, tarifas porcentuales, tarifas específicas y clasificación fiscal.
 */
public class HistorialIceProducto {

	private Long historialIceId;
	private Long productoId;
	private Boolean aplicaIceAnterior;
	private BigDecimal tarifaIceAnterior;
	private BigDecimal valorEspecificoAnterior;
	private String tipoTarifaAnterior;
	private Boolean aplicaIceNuevo;
	private BigDecimal tarifaIceNueva;
	private BigDecimal valorEspecificoNuevo;
	private String tipoTarifaNuevo;
	private String grupoIce;
	private Boolean esMonofasico;
	private String motivo;
	private String usuarioModificador;
	private OffsetDateTime fechaCambio;
	private String origenCambio;
	private String resolucionSri;
	private LocalDate fechaVigencia;
	private String sessionId;
	private String direccionIp;

	public HistorialIceProducto() {
	}

	public Long getHistorialIceId() {
		return historialIceId;
	}

	public void setHistorialIceId(Long historialIceId) {
		this.historialIceId = historialIceId;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public Boolean getAplicaIceAnterior() {
		return aplicaIceAnterior;
	}

	public void setAplicaIceAnterior(Boolean aplicaIceAnterior) {
		this.aplicaIceAnterior = aplicaIceAnterior;
	}

	public BigDecimal getTarifaIceAnterior() {
		return tarifaIceAnterior;
	}

	public void setTarifaIceAnterior(BigDecimal tarifaIceAnterior) {
		this.tarifaIceAnterior = tarifaIceAnterior;
	}

	public BigDecimal getValorEspecificoAnterior() {
		return valorEspecificoAnterior;
	}

	public void setValorEspecificoAnterior(BigDecimal valorEspecificoAnterior) {
		this.valorEspecificoAnterior = valorEspecificoAnterior;
	}

	public String getTipoTarifaAnterior() {
		return tipoTarifaAnterior;
	}

	public void setTipoTarifaAnterior(String tipoTarifaAnterior) {
		this.tipoTarifaAnterior = tipoTarifaAnterior;
	}

	public Boolean getAplicaIceNuevo() {
		return aplicaIceNuevo;
	}

	public void setAplicaIceNuevo(Boolean aplicaIceNuevo) {
		this.aplicaIceNuevo = aplicaIceNuevo;
	}

	public BigDecimal getTarifaIceNueva() {
		return tarifaIceNueva;
	}

	public void setTarifaIceNueva(BigDecimal tarifaIceNueva) {
		this.tarifaIceNueva = tarifaIceNueva;
	}

	public BigDecimal getValorEspecificoNuevo() {
		return valorEspecificoNuevo;
	}

	public void setValorEspecificoNuevo(BigDecimal valorEspecificoNuevo) {
		this.valorEspecificoNuevo = valorEspecificoNuevo;
	}

	public String getTipoTarifaNuevo() {
		return tipoTarifaNuevo;
	}

	public void setTipoTarifaNuevo(String tipoTarifaNuevo) {
		this.tipoTarifaNuevo = tipoTarifaNuevo;
	}

	public String getGrupoIce() {
		return grupoIce;
	}

	public void setGrupoIce(String grupoIce) {
		this.grupoIce = grupoIce;
	}

	public Boolean getEsMonofasico() {
		return esMonofasico;
	}

	public void setEsMonofasico(Boolean esMonofasico) {
		this.esMonofasico = esMonofasico;
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
