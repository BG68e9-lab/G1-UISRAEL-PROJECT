package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class OrdenCompraRequestDto {

    @NotNull(message = "El proveedorId es obligatorio")
    private Long proveedorId;

    private Integer negocioId;

@Valid
    private List<DetalleOrdenCompraRequestDto> detalles;

    public OrdenCompraRequestDto() {}

    public Long getProveedorId() { return proveedorId; }
    public void setProveedorId(Long proveedorId) { this.proveedorId = proveedorId; }

    public Integer getNegocioId() { return negocioId; }
    public void setNegocioId(Integer negocioId) { this.negocioId = negocioId; }

    public List<DetalleOrdenCompraRequestDto> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrdenCompraRequestDto> detalles) { this.detalles = detalles; }
}
