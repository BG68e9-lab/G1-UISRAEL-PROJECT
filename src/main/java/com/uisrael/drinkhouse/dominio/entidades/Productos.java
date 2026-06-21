package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Productos {

	private Integer productoId;
	private Integer negocioId;
	private Integer categoriaId;
	private String nombre;
	private String marca;
	private String tipo;
	private String descripcion;
	private BigDecimal costoPromedio;
	private BigDecimal margenGanancia;
	private BigDecimal precioVenta;
	private BigDecimal precioPersonalizado;
	private Integer stockActual;
	private Integer stockMinimo;
	private Boolean visibleSinStock;
	private String origenIdentificacion;

	public Productos(Integer productoId, Integer negocioId, Integer categoriaId, String nombre, String marca,
			String tipo, String descripcion, BigDecimal costoPromedio, BigDecimal margenGanancia, BigDecimal precioVenta,
			BigDecimal precioPersonalizado, Integer stockActual, Integer stockMinimo, Boolean visibleSinStock,
			String origenIdentificacion) {
		super();
		this.productoId = productoId;
		this.negocioId = negocioId;
		this.categoriaId = categoriaId;
		this.nombre = nombre;
		this.marca = marca;
		this.tipo = tipo;
		this.descripcion = descripcion;
		this.costoPromedio = costoPromedio;
		this.margenGanancia = margenGanancia;
		this.precioVenta = precioVenta;
		this.precioPersonalizado = precioPersonalizado;
		this.stockActual = stockActual;
		this.stockMinimo = stockMinimo;
		this.visibleSinStock = visibleSinStock;
		this.origenIdentificacion = origenIdentificacion;
	}

	public Productos() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getProductoId() {
		return productoId;
	}

	public void setProductoId(Integer productoId) {
		this.productoId = productoId;
	}

	public Integer getNegocioId() {
		return negocioId;
	}

	public void setNegocioId(Integer negocioId) {
		this.negocioId = negocioId;
	}

	public Integer getCategoriaId() {
		return categoriaId;
	}

	public void setCategoriaId(Integer categoriaId) {
		this.categoriaId = categoriaId;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public BigDecimal getCostoPromedio() {
		return costoPromedio;
	}

	public void setCostoPromedio(BigDecimal costoPromedio) {
		this.costoPromedio = costoPromedio;
	}

	public BigDecimal getMargenGanancia() {
		return margenGanancia;
	}

	public void setMargenGanancia(BigDecimal margenGanancia) {
		this.margenGanancia = margenGanancia;
	}

	public BigDecimal getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(BigDecimal precioVenta) {
		this.precioVenta = precioVenta;
	}

	public BigDecimal getPrecioPersonalizado() {
		return precioPersonalizado;
	}

	public void setPrecioPersonalizado(BigDecimal precioPersonalizado) {
		this.precioPersonalizado = precioPersonalizado;
	}

	public Integer getStockActual() {
		return stockActual;
	}

	public void setStockActual(Integer stockActual) {
		this.stockActual = stockActual;
	}

	public Integer getStockMinimo() {
		return stockMinimo;
	}

	public void setStockMinimo(Integer stockMinimo) {
		this.stockMinimo = stockMinimo;
	}

	public Boolean getVisibleSinStock() {
		return visibleSinStock;
	}

	public void setVisibleSinStock(Boolean visibleSinStock) {
		this.visibleSinStock = visibleSinStock;
	}

	public String getOrigenIdentificacion() {
		return origenIdentificacion;
	}

	public void setOrigenIdentificacion(String origenIdentificacion) {
		this.origenIdentificacion = origenIdentificacion;
	}

}
