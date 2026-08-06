package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;

public class Producto {

	private Long productoId;
	private Integer negocioId;
	private Long categoriaId;
	private String categoriaNombre;
	private Long tipoProductoId;
	private String tipoProductoNombre;
	private Boolean activo;
	private String nombre;
	private String marca;
	private String descripcion;
	private BigDecimal costoPromedio;
	private BigDecimal margenGanancia;
	private BigDecimal precioVenta;
	private Boolean precioPersonalizado;
	private Integer stockActual;
	private Integer stockMinimo;
	private Boolean visibleSinStock;
	private Boolean permiteStockNegativo;
	private String origenIdentificacion;

	public Producto() {
	}

	public Producto(Long productoId, String nombre, String marca, String descripcion,
			BigDecimal costoPromedio, BigDecimal margenGanancia, BigDecimal precioVenta,
			Boolean precioPersonalizado, Integer stockActual, Integer stockMinimo,
			Boolean visibleSinStock, String origenIdentificacion) {
		this.productoId = productoId;
		this.nombre = nombre;
		this.marca = marca;
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

	public Long getProductoId() { return productoId; }
	public void setProductoId(Long productoId) { this.productoId = productoId; }
	public Integer getNegocioId() { return negocioId; }
	public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }

	public Long getCategoriaId() { return categoriaId; }
	public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

	public String getCategoriaNombre() { return categoriaNombre; }
	public void setCategoriaNombre(String categoriaNombre) { this.categoriaNombre = categoriaNombre; }

	public Long getTipoProductoId() { return tipoProductoId; }
	public void setTipoProductoId(Long tipoProductoId) { this.tipoProductoId = tipoProductoId; }

	public String getTipoProductoNombre() { return tipoProductoNombre; }
	public void setTipoProductoNombre(String tipoProductoNombre) { this.tipoProductoNombre = tipoProductoNombre; }

	public Boolean getActivo() { return activo; }
	public void setActivo(Boolean activo) { this.activo = activo; }

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public String getMarca() { return marca; }
	public void setMarca(String marca) { this.marca = marca; }

	public String getDescripcion() { return descripcion; }
	public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

	public BigDecimal getCostoPromedio() { return costoPromedio; }
	public void setCostoPromedio(BigDecimal costoPromedio) { this.costoPromedio = costoPromedio; }

	public BigDecimal getMargenGanancia() { return margenGanancia; }
	public void setMargenGanancia(BigDecimal margenGanancia) { this.margenGanancia = margenGanancia; }

	public BigDecimal getPrecioVenta() { return precioVenta; }
	public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

	public Boolean getPrecioPersonalizado() { return precioPersonalizado; }
	public void setPrecioPersonalizado(Boolean precioPersonalizado) { this.precioPersonalizado = precioPersonalizado; }

	public Integer getStockActual() { return stockActual; }
	public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

	public Integer getStockMinimo() { return stockMinimo; }
	public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

	public Boolean getVisibleSinStock() { return visibleSinStock; }
	public void setVisibleSinStock(Boolean visibleSinStock) { this.visibleSinStock = visibleSinStock; }

	public Boolean getPermiteStockNegativo() { return permiteStockNegativo; }
	public void setPermiteStockNegativo(Boolean permiteStockNegativo) { this.permiteStockNegativo = permiteStockNegativo; }

	public String getOrigenIdentificacion() { return origenIdentificacion; }
	public void setOrigenIdentificacion(String origenIdentificacion) { this.origenIdentificacion = origenIdentificacion; }
}
