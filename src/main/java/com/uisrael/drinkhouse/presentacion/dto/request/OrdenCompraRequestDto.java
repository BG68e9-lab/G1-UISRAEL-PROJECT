package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para crear o actualizar una Orden de Compra.
 */
public class OrdenCompraRequestDto {

    @NotNull(message = "El proveedorId es obligatorio")
    private Long proveedorId;

    private Integer negocioId;

    @NotEmpty(message = "La lista de detalles no puede estar vacía")
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
