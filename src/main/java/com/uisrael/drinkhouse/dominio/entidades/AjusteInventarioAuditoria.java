package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad de dominio para el registro de auditoría de ajustes de inventario.
 * Mantiene un registro completo de cada movimiento con detalles de autorización y justificación.
 */
public class AjusteInventarioAuditoria {

	private Long ajusteId;
	private Long movimientoId;
	private Long productoId;
	private Long loteId;
	private String tipoMovimiento;
	private BigDecimal cantidadAnterior;
	private BigDecimal ajuste;
	private BigDecimal cantidadPosterior;
	private String usuarioAutorizado;
	private String usuarioEjecutor;
	private String justificacion;
	private OffsetDateTime fechaHora;
	private String direccionIp;
	private String sessionId;
	private Long ventaId;

	public AjusteInventarioAuditoria() {
	}

	public AjusteInventarioAuditoria(Long ajusteId, Long movimientoId, Long productoId,
			Long loteId, String tipoMovimiento, BigDecimal cantidadAnterior,
			BigDecimal ajuste, BigDecimal cantidadPosterior, String usuarioAutorizado,
			String usuarioEjecutor, String justificacion, OffsetDateTime fechaHora,
			String direccionIp, String sessionId, Long ventaId) {
		this.ajusteId = ajusteId;
		this.movimientoId = movimientoId;
		this.productoId = productoId;
		this.loteId = loteId;
		this.tipoMovimiento = tipoMovimiento;
		this.cantidadAnterior = cantidadAnterior;
		this.ajuste = ajuste;
		this.cantidadPosterior = cantidadPosterior;
		this.usuarioAutorizado = usuarioAutorizado;
		this.usuarioEjecutor = usuarioEjecutor;
		this.justificacion = justificacion;
		this.fechaHora = fechaHora;
		this.direccionIp = direccionIp;
		this.sessionId = sessionId;
		this.ventaId = ventaId;
	}

	public Long getAjusteId() {
		return ajusteId;
	}

	public void setAjusteId(Long ajusteId) {
		this.ajusteId = ajusteId;
	}

	public Long getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Long movimientoId) {
		this.movimientoId = movimientoId;
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

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

	public BigDecimal getCantidadAnterior() {
		return cantidadAnterior;
	}

	public void setCantidadAnterior(BigDecimal cantidadAnterior) {
		this.cantidadAnterior = cantidadAnterior;
	}

	public BigDecimal getAjuste() {
		return ajuste;
	}

	public void setAjuste(BigDecimal ajuste) {
		this.ajuste = ajuste;
	}

	public BigDecimal getCantidadPosterior() {
		return cantidadPosterior;
	}

	public void setCantidadPosterior(BigDecimal cantidadPosterior) {
		this.cantidadPosterior = cantidadPosterior;
	}

	public String getUsuarioAutorizado() {
		return usuarioAutorizado;
	}

	public void setUsuarioAutorizado(String usuarioAutorizado) {
		this.usuarioAutorizado = usuarioAutorizado;
	}

	public String getUsuarioEjecutor() {
		return usuarioEjecutor;
	}

	public void setUsuarioEjecutor(String usuarioEjecutor) {
		this.usuarioEjecutor = usuarioEjecutor;
	}

	public String getJustificacion() {
		return justificacion;
	}

	public void setJustificacion(String justificacion) {
		this.justificacion = justificacion;
	}

	public OffsetDateTime getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(OffsetDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	public String getDireccionIp() {
		return direccionIp;
	}

	public void setDireccionIp(String direccionIp) {
		this.direccionIp = direccionIp;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Long getVentaId() {
		return ventaId;
	}

	public void setVentaId(Long ventaId) {
		this.ventaId = ventaId;
	}
}
