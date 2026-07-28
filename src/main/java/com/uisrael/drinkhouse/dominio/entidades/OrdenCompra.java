package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenCompra {

	private Long ordenCompraId;
	private String codigoReferencia;
	private Long proveedorId;
	private String proveedorRazonSocial;
	private Integer negocioId;
	private String estado;
	private BigDecimal total;
	private LocalDateTime fechaCreacion;
	private String usuarioCreacion;
	private String observaciones;
	private Long version;
	private List<DetalleOrdenCompra> detalles = new ArrayList<>();

	public OrdenCompra() {
	}

	public void calcularTotal() {
		BigDecimal suma = BigDecimal.ZERO;
		if (detalles != null) {
			for (DetalleOrdenCompra detalle : detalles) {
				detalle.calcularSubtotal();
				if (detalle.getSubtotal() != null) {
					suma = suma.add(detalle.getSubtotal());
				}
			}
		}
		this.total = suma;
	}

	public Long getOrdenCompraId() {
		return ordenCompraId;
	}

	public void setOrdenCompraId(Long ordenCompraId) {
		this.ordenCompraId = ordenCompraId;
	}

	public String getCodigoReferencia() {
		return codigoReferencia;
	}

	public void setCodigoReferencia(String codigoReferencia) {
		this.codigoReferencia = codigoReferencia;
	}

	public Long getProveedorId() {
		return proveedorId;
	}

	public void setProveedorId(Long proveedorId) {
		this.proveedorId = proveedorId;
	}

	public String getProveedorRazonSocial() {
		return proveedorRazonSocial;
	}

	public void setProveedorRazonSocial(String proveedorRazonSocial) {
		this.proveedorRazonSocial = proveedorRazonSocial;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(LocalDateTime fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getUsuarioCreacion() {
		return usuarioCreacion;
	}

	public void setUsuarioCreacion(String usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public List<DetalleOrdenCompra> getDetalles() {
		return detalles;
	}

	public void setDetalles(List<DetalleOrdenCompra> detalles) {
		this.detalles = detalles;
	}
}
