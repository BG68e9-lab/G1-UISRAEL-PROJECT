package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO de respuesta para una Orden de Compra.
 */
public class OrdenCompraResponseDto {

    private Long ordenCompraId;
    private String codigoReferencia;
    private String estado;
    private Double total;
    private OffsetDateTime creadoEn;
    private OffsetDateTime fechaCreacion;
    private Long proveedorId;
    private String proveedorRazonSocial;
    private List<DetalleOrdenCompraResponseDto> detalles;

    public OrdenCompraResponseDto() {}

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

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public String getProveedorRazonSocial() { return proveedorRazonSocial; }
    public void setProveedorRazonSocial(String proveedorRazonSocial) { this.proveedorRazonSocial = proveedorRazonSocial; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<DetalleOrdenCompraResponseDto> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrdenCompraResponseDto> detalles) { this.detalles = detalles; }
}
