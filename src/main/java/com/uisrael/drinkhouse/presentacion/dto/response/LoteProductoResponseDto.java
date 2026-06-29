package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class LoteProductoResponseDto {

	private Integer loteId;
	private Integer negocioId;
	private Integer ordenCompraId;
	private String codigoEntrada;
	private Integer cantidadInicial;
	private Integer cantidadDisponible;
	private BigDecimal precioCosto;
	private OffsetDateTime fechaIngreso;
	private OffsetDateTime fechaVencimiento;
	private Integer estadoRespaldoId;
	private String registradoPor;

	public Integer getLoteId() {
		return loteId;
	}

	public void setLoteId(Integer loteId) {
		this.loteId = loteId;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public Integer getOrdenCompraId() {
		return ordenCompraId;
	}

	public void setOrdenCompraId(Integer ordenCompraId) {
		this.ordenCompraId = ordenCompraId;
	}

	public String getCodigoEntrada() {
		return codigoEntrada;
	}

	public void setCodigoEntrada(String codigoEntrada) {
		this.codigoEntrada = codigoEntrada;
	}

	public Integer getCantidadInicial() {
		return cantidadInicial;
	}

	public void setCantidadInicial(Integer cantidadInicial) {
		this.cantidadInicial = cantidadInicial;
	}

	public Integer getCantidadDisponible() {
		return cantidadDisponible;
	}

	public void setCantidadDisponible(Integer cantidadDisponible) {
		this.cantidadDisponible = cantidadDisponible;
	}

	public BigDecimal getPrecioCosto() {
		return precioCosto;
	}

	public void setPrecioCosto(BigDecimal precioCosto) {
		this.precioCosto = precioCosto;
	}

	public OffsetDateTime getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(OffsetDateTime fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public OffsetDateTime getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(OffsetDateTime fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public Integer getEstadoRespaldoId() {
		return estadoRespaldoId;
	}

	public void setEstadoRespaldoId(Integer estadoRespaldoId) {
		this.estadoRespaldoId = estadoRespaldoId;
	}

	public String getRegistradoPor() {
		return registradoPor;
	}

	public void setRegistradoPor(String registradoPor) {
		this.registradoPor = registradoPor;
	}

}
