package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para un Detalle de Orden de Compra.
 */
public class DetalleOrdenCompraResponseDto {

    private Long detalleOrdenCompraId;
    private Long ordenCompraId;
    private Long productoId;
    private String productoNombre;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private LocalDate fechaVencimiento;

    public DetalleOrdenCompraResponseDto() {}

    public Long getDetalleOrdenCompraId() { return detalleOrdenCompraId; }
    public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) { this.detalleOrdenCompraId = detalleOrdenCompraId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
}
