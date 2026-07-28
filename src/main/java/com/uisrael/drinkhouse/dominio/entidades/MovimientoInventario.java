package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimientoInventario {

	private Long movimientoId;
	private String codigoMovimiento;
	private String tipo;
	private Long productoId;
	private String productoNombre;
	private String productoMarca;
	private String productoTipo;
	private Long loteId;
	private String loteCodigoEntrada;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private String descripcion;
	private LocalDateTime fechaMovimiento;
	private String usuarioCreacion;

	public MovimientoInventario() {
	}

	public BigDecimal getValorTotal() {
		if (cantidad == null || precioUnitario == null) {
			return null;
		}
		return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
	}

	public Long getMovimientoId() {
		return movimientoId;
	}

	public void setMovimientoId(Long movimientoId) {
		this.movimientoId = movimientoId;
	}

	public String getCodigoMovimiento() {
		return codigoMovimiento;
	}

	public void setCodigoMovimiento(String codigoMovimiento) {
		this.codigoMovimiento = codigoMovimiento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Long getProductoId() {
		return productoId;
	}

	public void setProductoId(Long productoId) {
		this.productoId = productoId;
	}

	public String getProductoNombre() {
		return productoNombre;
	}

	public void setProductoNombre(String productoNombre) {
		this.productoNombre = productoNombre;
	}

	public String getProductoMarca() {
		return productoMarca;
	}

	public void setProductoMarca(String productoMarca) {
		this.productoMarca = productoMarca;
	}

	public String getProductoTipo() {
		return productoTipo;
	}

	public void setProductoTipo(String productoTipo) {
		this.productoTipo = productoTipo;
	}

	public Long getLoteId() {
		return loteId;
	}

	public void setLoteId(Long loteId) {
		this.loteId = loteId;
	}

	public String getLoteCodigoEntrada() {
		return loteCodigoEntrada;
	}

	public void setLoteCodigoEntrada(String loteCodigoEntrada) {
		this.loteCodigoEntrada = loteCodigoEntrada;
	}

	public Integer getCantidad() {
		return cantidad;
	}

	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(BigDecimal precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDateTime getFechaMovimiento() {
		return fechaMovimiento;
	}

	public void setFechaMovimiento(LocalDateTime fechaMovimiento) {
		this.fechaMovimiento = fechaMovimiento;
	}

	public String getUsuarioCreacion() {
		return usuarioCreacion;
	}

	public void setUsuarioCreacion(String usuarioCreacion) {
		this.usuarioCreacion = usuarioCreacion;
	}
}
