package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;
import java.util.UUID;

public class OrdenCompra {
	
	private Long ordenCompraId;
	private Integer negocioId;
	private String codigoReferencia;
	private String estado;
	private Double total;
	private OffsetDateTime creadoEn;
	private UUID confirmadoPor;
	private OffsetDateTime confirmadoEn;
	
	public OrdenCompra() {}

	public OrdenCompra(Long ordenCompraId, Integer negocioId, String codigoReferencia, String estado, Double total, 
			OffsetDateTime creadoEn, UUID confirmadoPor, OffsetDateTime confirmadoEn) {
		this.ordenCompraId = ordenCompraId;
		this.negocioId = negocioId;
		this.codigoReferencia = codigoReferencia;
		this.estado = estado;
		this.total = total;
		this.creadoEn = creadoEn;
		this.confirmadoPor = confirmadoPor;
		this.confirmadoEn = confirmadoEn;
	}

	public Long getOrdenCompraId() { return ordenCompraId; }
	public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }
	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }
	public String getCodigoReferencia() { return codigoReferencia; }
	public void setCodigoReferencia(String codigoReferencia) { this.codigoReferencia = codigoReferencia; }
	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }
	public Double getTotal() { return total; }
	public void setTotal(Double total) { this.total = total; }
	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }
	public UUID getConfirmadoPor() { return confirmadoPor; }
	public void setConfirmadoPor(UUID confirmadoPor) { this.confirmadoPor = confirmadoPor; }
	public OffsetDateTime getConfirmadoEn() { return confirmadoEn; }
	public void setConfirmadoEn(OffsetDateTime confirmadoEn) { this.confirmadoEn = confirmadoEn; }
}