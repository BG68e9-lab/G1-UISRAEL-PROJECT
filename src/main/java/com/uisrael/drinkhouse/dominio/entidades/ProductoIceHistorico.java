package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Tasa de ICE (Impuesto a Consumos Especiales) vigente para un producto en un
 * periodo de tiempo. A diferencia del IVA, el ICE depende del tipo de
 * producto (ej: grado de alcohol, tipo de bebida), asi que se lleva por
 * producto y no de forma global. Puede ser porcentual (ad-valorem, % sobre
 * precioVenta) o especifico (monto fijo, ej: por litro de alcohol puro).
 */
public class ProductoIceHistorico {

	public static final String TIPO_PORCENTUAL = "PORCENTUAL";
	public static final String TIPO_ESPECIFICO = "ESPECIFICO";

	private Long id;
	private Long productoId;
	private String tipoIce;
	private BigDecimal valor;
	private OffsetDateTime vigenteDesde;
	private OffsetDateTime vigenteHasta;
	private String motivo;
	private OffsetDateTime creadoEn;

	public ProductoIceHistorico() {
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

	public String getTipoIce() {
		return tipoIce;
	}

	public void setTipoIce(String tipoIce) {
		this.tipoIce = tipoIce;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
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
