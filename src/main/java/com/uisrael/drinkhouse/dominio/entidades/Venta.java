package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entidad de dominio para ventas realizadas.
 * Cada venta registra el cliente, fecha, total y estado.
 */
public class Venta {

	private Long ventaId;
	private Integer negocioId;
	private String codigoVenta;
	private String nombreCliente;
	private String cedulaRuc;
	private String telefono;
	private String email;
	private String metodoPago;
	private BigDecimal subtotal;
	private BigDecimal descuento;
	private BigDecimal iva;
	private BigDecimal total;
	private String estado;
	private String observaciones;
	private OffsetDateTime creadoEn;
	private OffsetDateTime anuladoEn;

	public Venta() {
	}

	public Long getVentaId() { return ventaId; }
	public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }

	public String getCodigoVenta() { return codigoVenta; }
	public void setCodigoVenta(String codigoVenta) { this.codigoVenta = codigoVenta; }

	public String getNombreCliente() { return nombreCliente; }
	public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

	public String getCedulaRuc() { return cedulaRuc; }
	public void setCedulaRuc(String cedulaRuc) { this.cedulaRuc = cedulaRuc; }

	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }

	public String getMetodoPago() { return metodoPago; }
	public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

	public BigDecimal getSubtotal() { return subtotal; }
	public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

	public BigDecimal getDescuento() { return descuento; }
	public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }

	public BigDecimal getIva() { return iva; }
	public void setIva(BigDecimal iva) { this.iva = iva; }

	public BigDecimal getTotal() { return total; }
	public void setTotal(BigDecimal total) { this.total = total; }

	public String getEstado() { return estado; }
	public void setEstado(String estado) { this.estado = estado; }

	public String getObservaciones() { return observaciones; }
	public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

	public OffsetDateTime getCreadoEn() { return creadoEn; }
	public void setCreadoEn(OffsetDateTime creadoEn) { this.creadoEn = creadoEn; }

	public OffsetDateTime getAnuladoEn() { return anuladoEn; }
	public void setAnuladoEn(OffsetDateTime anuladoEn) { this.anuladoEn = anuladoEn; }
}
