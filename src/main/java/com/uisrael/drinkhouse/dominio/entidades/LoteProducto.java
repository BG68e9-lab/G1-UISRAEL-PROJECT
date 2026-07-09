package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import java.time.LocalDate;

public class LoteProducto {

	private Long loteId;
	private String codigoEntrada;
	private BigDecimal cantidadInicial;
	private BigDecimal cantidadDisponible;
	private BigDecimal precioCosto;
	private OffsetDateTime fechaIngreso;
	private LocalDate fechaVencimiento;

	public LoteProducto() {
	}

	public LoteProducto(Long loteId, String codigoEntrada, BigDecimal cantidadInicial,
			BigDecimal cantidadDisponible, BigDecimal precioCosto,
			OffsetDateTime fechaIngreso, LocalDate fechaVencimiento) {
		this.loteId = loteId;
		this.codigoEntrada = codigoEntrada;
		this.cantidadInicial = cantidadInicial;
		this.cantidadDisponible = cantidadDisponible;
		this.precioCosto = precioCosto;
		this.fechaIngreso = fechaIngreso;
		this.fechaVencimiento = fechaVencimiento;
	}

	public Long getLoteId() { return loteId; }
	public void setLoteId(Long loteId) { this.loteId = loteId; }

	public String getCodigoEntrada() { return codigoEntrada; }
	public void setCodigoEntrada(String codigoEntrada) { this.codigoEntrada = codigoEntrada; }

	public BigDecimal getCantidadInicial() { return cantidadInicial; }
	public void setCantidadInicial(BigDecimal cantidadInicial) { this.cantidadInicial = cantidadInicial; }

	public BigDecimal getCantidadDisponible() { return cantidadDisponible; }
	public void setCantidadDisponible(BigDecimal cantidadDisponible) { this.cantidadDisponible = cantidadDisponible; }

	public BigDecimal getPrecioCosto() { return precioCosto; }
	public void setPrecioCosto(BigDecimal precioCosto) { this.precioCosto = precioCosto; }

	public OffsetDateTime getFechaIngreso() { return fechaIngreso; }
	public void setFechaIngreso(OffsetDateTime fechaIngreso) { this.fechaIngreso = fechaIngreso; }

	public LocalDate getFechaVencimiento() { return fechaVencimiento; }
	public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
