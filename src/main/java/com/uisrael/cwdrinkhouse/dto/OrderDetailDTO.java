package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Data Transfer Object for Order Detail (line item) information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 5.1-5.14, 14.1-14.10
 */
public class OrderDetailDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Order detail identifier (auto-generated).
     */
    private Long detalleId;

    /**
     * Parent order identifier.
     * Set automatically when detail is added to order.
     */
    private Long ordenCompraId;

    /**
     * Product identifier.
     * Required field linking to product.
     */
    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    /**
     * Product information (for display purposes).
     * Not validated as it's populated from backend.
     */
    private String productoNombre;
    private String productoMarca;
    private String productoTipo;

    /**
     * Quantity ordered.
     * Must be a positive integer.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser un número positivo")
    @Max(value = 999999, message = "La cantidad no puede ser mayor a 999,999")
    private Integer cantidad;

    /**
     * Unit price for this order.
     * Must be a positive decimal value.
     */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener máximo 2 decimales")
    private BigDecimal precioUnitario;

    /**
     * Subtotal for this line item (calculated).
     * cantidad × precioUnitario
     */
    private BigDecimal subtotal;

    /**
     * Notes or comments for this detail.
     */
    @Size(max = 200, message = "Las observaciones no deben exceder los 200 caracteres")
    private String observaciones;

    /**
     * Default constructor.
     */
    public OrderDetailDTO() {
        this.subtotal = BigDecimal.ZERO;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param productoId the product ID
     * @param cantidad the quantity
     * @param precioUnitario the unit price
     */
    public OrderDetailDTO(Long productoId, Integer cantidad, BigDecimal precioUnitario) {
        this();
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calculateSubtotal();
    }

    /**
     * Calculates the subtotal for this line item.
     * cantidad × precioUnitario
     * 
     * @return the calculated subtotal
     */
    public BigDecimal calculateSubtotal() {
        if (cantidad != null && precioUnitario != null) {
            this.subtotal = BigDecimal.valueOf(cantidad).multiply(precioUnitario);
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
        return this.subtotal;
    }

    /**
     * Updates quantity and recalculates subtotal.
     * 
     * @param newCantidad the new quantity
     */
    public void updateCantidad(Integer newCantidad) {
        this.cantidad = newCantidad;
        calculateSubtotal();
    }

    /**
     * Updates unit price and recalculates subtotal.
     * 
     * @param newPrecio the new unit price
     */
    public void updatePrecioUnitario(BigDecimal newPrecio) {
        this.precioUnitario = newPrecio;
        calculateSubtotal();
    }

    /**
     * Gets formatted product display name.
     * Format: "ProductName - Brand (Type)"
     * 
     * @return formatted product display name
     */
    public String getProductoDisplayName() {
        StringBuilder sb = new StringBuilder();
        if (productoNombre != null) {
            sb.append(productoNombre);
        }
        if (productoMarca != null) {
            sb.append(" - ").append(productoMarca);
        }
        if (productoTipo != null) {
            sb.append(" (").append(productoTipo).append(")");
        }
        return sb.toString();
    }

    // Getters and Setters

    public Long getDetalleId() {
        return detalleId;
    }

    public void setDetalleId(Long detalleId) {
        this.detalleId = detalleId;
    }

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        calculateSubtotal(); // Recalculate when quantity changes
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calculateSubtotal(); // Recalculate when price changes
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return "OrderDetailDTO{" +
                "detalleId=" + detalleId +
                ", ordenCompraId=" + ordenCompraId +
                ", productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", productoMarca='" + productoMarca + '\'' +
                ", productoTipo='" + productoTipo + '\'' +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", subtotal=" + subtotal +
                ", observaciones='" + observaciones + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDetailDTO that = (OrderDetailDTO) o;
        return detalleId != null ? detalleId.equals(that.detalleId) : that.detalleId == null;
    }

    @Override
    public int hashCode() {
        return detalleId != null ? detalleId.hashCode() : 0;
    }
}