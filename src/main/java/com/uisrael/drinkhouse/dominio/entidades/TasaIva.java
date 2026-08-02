package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Tasa de IVA vigente en un periodo de tiempo. Es global (no por producto):
 * el IVA lo fija el estado y aplica igual a todos los productos, salvo los
 * marcados como exentos (ver Producto.ivaExento). Cada vez que cambia la
 * tasa (ej: 12% -> 15%) se cierra el registro anterior (vigenteHasta) y se
 * crea uno nuevo, conservando el historico completo.
 */
public class TasaIva {

	private Long tasaIvaId;
	private BigDecimal porcentaje;
	private OffsetDateTime vigenteDesde;
	private OffsetDateTime vigenteHasta;
	private String motivo;
	private OffsetDateTime creadoEn;

	public TasaIva() {
	}

	public Long getTasaIvaId() {
		return tasaIvaId;
	}

	public void setTasaIvaId(Long tasaIvaId) {
		this.tasaIvaId = tasaIvaId;
	}

	public BigDecimal getPorcentaje() {
		return porcentaje;
	}

	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
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
