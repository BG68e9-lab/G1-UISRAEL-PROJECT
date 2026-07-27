package com.uisrael.drinkhouse.presentacion.dto.response;

/**
 * DTO de respuesta para un Detalle de Orden de Compra.
 */
public class DetalleOrdenCompraResponseDto {

    private Long detalleOrdenCompraId;
    private Long ordenCompraId;
    private Long productoId;
    private Integer cantidad;
    private Double precioUnitario;

    public DetalleOrdenCompraResponseDto() {}

    public Long getDetalleOrdenCompraId() { return detalleOrdenCompraId; }
    public void setDetalleOrdenCompraId(Long detalleOrdenCompraId) { this.detalleOrdenCompraId = detalleOrdenCompraId; }

    public Long getOrdenCompraId() { return ordenCompraId; }
    public void setOrdenCompraId(Long ordenCompraId) { this.ordenCompraId = ordenCompraId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }
}
