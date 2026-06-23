package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

public class OrdenCompra {
	
	private Long ordenCompraId;
	private String codigoReferencia;
	private String estado;
	private Double total;
	private OffsetDateTime creadoEn;
	
	public OrdenCompra() {}

	public OrdenCompra(Long ordenCompraId, String codigoReferencia, String estado, Double total, OffsetDateTime creadoEn) {
		this.ordenCompraId = ordenCompraId;
		this.codigoReferencia = codigoReferencia;
		this.estado = estado;
		this.total = total;
		this.creadoEn = creadoEn;
	}

	public Long getOrdenCompraId() { return ordenCompraId; }
	public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }
	public String getCodigoReferencia() { return codigoReferencia; }
	public void setCodigoReferencia(String codigoReferencia) { this.codigoReferencia = codigoReferencia; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public Double getTotal() { return total; }
	public void setTotal(Double total) { this.total = total; }
	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
}