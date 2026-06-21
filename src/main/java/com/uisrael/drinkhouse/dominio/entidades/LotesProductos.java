package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.util.Date;


public class LotesProductos {

	private Integer loteId;
	private Integer negocioId;
	private Integer ordenCompraId;
	private String codigoEntrada;
	private Integer cantidadInicial;
	private Integer cantidadDisponible;
	private BigDecimal precioCosto;
	private Date fechaIngreso;
	private Date fechaVencimiento;
	private Integer estadoRespaldoId;
	private String registradoPor;

	public LotesProductos(Integer loteId, Integer negocioId, Integer ordenCompraId, String codigoEntrada,
			Integer cantidadInicial, Integer cantidadDisponible, BigDecimal precioCosto, Date fechaIngreso,
			Date fechaVencimiento, Integer estadoRespaldoId, String registradoPor) {
		super();
		this.loteId = loteId;
		this.negocioId = negocioId;
		this.ordenCompraId = ordenCompraId;
		this.codigoEntrada = codigoEntrada;
		this.cantidadInicial = cantidadInicial;
		this.cantidadDisponible = cantidadDisponible;
		this.precioCosto = precioCosto;
		this.fechaIngreso = fechaIngreso;
		this.fechaVencimiento = fechaVencimiento;
		this.estadoRespaldoId = estadoRespaldoId;
		this.registradoPor = registradoPor;
	}

	public LotesProductos() {
		super();
		// TODO Auto-generated constructor stub
	}

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

	public Date getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(Date fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(Date fechaVencimiento) {
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
