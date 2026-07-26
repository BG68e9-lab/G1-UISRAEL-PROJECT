package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Inventory Movement information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 7.1-7.12, 14.1-14.10
 */
public class InventoryMovementDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Movement identifier (auto-generated).
     */
    private Long movimientoId;

    /**
     * Movement reference code.
     * Auto-generated with format based on type:
     * ENT-YYYYMMDD-HHMMSS (ENTRADA)
     * SAL-YYYYMMDD-HHMMSS (SALIDA)
     * AJU-YYYYMMDD-HHMMSS (AJUSTE)
     */
    private String codigoMovimiento;

    /**
     * Movement type.
     * Valid values: ENTRADA, SALIDA, AJUSTE
     */
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Pattern(regexp = "^(ENTRADA|SALIDA|AJUSTE)$", 
             message = "El tipo debe ser ENTRADA, SALIDA o AJUSTE")
    private String tipo;

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
     * Lot identifier.
     * Required for ENTRADA and SALIDA types.
     * Optional for AJUSTE type.
     */
    private Long loteId;

    /**
     * Lot information (for display purposes).
     */
    private String loteCodigoEntrada;

    /**
     * Movement quantity.
     * For ENTRADA and SALIDA: must be positive
     * For AJUSTE: can be positive (increase) or negative (decrease)
     */
    @NotNull(message = "La cantidad es obligatoria")
    private Integer cantidad;

    /**
     * Unit price for ENTRADA movements.
     * Required only for ENTRADA type.
     */
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero", groups = EntradaValidation.class)
    @Digits(integer = 10, fraction = 2, message = "El precio unitario debe tener máximo 2 decimales")
    private BigDecimal precioUnitario;

    /**
     * Total value of the movement (calculated).
     * cantidad × precioUnitario (for ENTRADA)
     */
    private BigDecimal valorTotal;

    /**
     * Reason or description for the movement.
     */
    @Size(max = 500, message = "La descripción no debe exceder los 500 caracteres")
    private String descripcion;

    /**
     * Movement timestamp (auto-generated).
     */
    private LocalDateTime fechaMovimiento;

    /**
     * User who created the movement (read-only).
     */
    private String usuarioCreacion;

    /**
     * Reference to related order (for ENTRADA from order reception).
     */
    private Long ordenCompraId;

    /**
     * Validation group for ENTRADA specific fields.
     */
    public interface EntradaValidation {
    }

    /**
     * Default constructor.
     */
    public InventoryMovementDTO() {
        this.fechaMovimiento = LocalDateTime.now();
        this.valorTotal = BigDecimal.ZERO;
    }

    /**
     * Constructor for ENTRADA movement.
     * 
     * @param productoId the product ID
     * @param loteId the lot ID
     * @param cantidad the quantity (positive)
     * @param precioUnitario the unit price
     * @param descripcion the description
     */
    public InventoryMovementDTO(Long productoId, Long loteId, Integer cantidad, 
                               BigDecimal precioUnitario, String descripcion) {
        this();
        this.tipo = "ENTRADA";
        this.productoId = productoId;
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.descripcion = descripcion;
        calculateValorTotal();
    }

    /**
     * Constructor for SALIDA movement.
     * 
     * @param productoId the product ID
     * @param loteId the lot ID
     * @param cantidad the quantity (positive)
     * @param descripcion the description
     */
    public InventoryMovementDTO(Long productoId, Long loteId, Integer cantidad, String descripcion) {
        this();
        this.tipo = "SALIDA";
        this.productoId = productoId;
        this.loteId = loteId;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    /**
     * Calculates the total value for the movement.
     * cantidad × precioUnitario (only for ENTRADA)
     * 
     * @return the calculated total value
     */
    public BigDecimal calculateValorTotal() {
        if ("ENTRADA".equals(tipo) && cantidad != null && precioUnitario != null) {
            this.valorTotal = BigDecimal.valueOf(cantidad).multiply(precioUnitario);
        } else {
            this.valorTotal = BigDecimal.ZERO;
        }
        return this.valorTotal;
    }

    /**
     * Validates type-specific business rules.
     * 
     * @return true if validation passes, false otherwise
     */
    public boolean isValidMovement() {
        if (tipo == null) {
            return false;
        }

        return switch (tipo) {
            case "ENTRADA" -> validateEntrada();
            case "SALIDA" -> validateSalida();
            case "AJUSTE" -> validateAjuste();
            default -> false;
        };
    }

    /**
     * Validates ENTRADA specific rules.
     * - Must have loteId
     * - Must have positive cantidad
     * - Must have positive precioUnitario
     */
    private boolean validateEntrada() {
        return loteId != null && 
               cantidad != null && cantidad > 0 &&
               precioUnitario != null && precioUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Validates SALIDA specific rules.
     * - Must have loteId
     * - Must have positive cantidad
     */
    private boolean validateSalida() {
        return loteId != null && cantidad != null && cantidad > 0;
    }

    /**
     * Validates AJUSTE specific rules.
     * - Cantidad can be positive or negative, but not zero
     */
    private boolean validateAjuste() {
        return cantidad != null && cantidad != 0;
    }

    /**
     * Gets validation error message if movement is invalid.
     * 
     * @return error message or null if valid
     */
    public String getValidationError() {
        if (tipo == null) {
            return "El tipo de movimiento es obligatorio";
        }

        return switch (tipo) {
            case "ENTRADA" -> {
                if (loteId == null) yield "El lote es obligatorio para movimientos de entrada";
                if (cantidad == null || cantidad <= 0) yield "La cantidad debe ser mayor a cero para entradas";
                if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) 
                    yield "El precio unitario es obligatorio y debe ser mayor a cero para entradas";
                yield null;
            }
            case "SALIDA" -> {
                if (loteId == null) yield "El lote es obligatorio para movimientos de salida";
                if (cantidad == null || cantidad <= 0) yield "La cantidad debe ser mayor a cero para salidas";
                yield null;
            }
            case "AJUSTE" -> {
                if (cantidad == null || cantidad == 0) yield "La cantidad debe ser diferente a cero para ajustes";
                yield null;
            }
            default -> "Tipo de movimiento no válido";
        };
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
        calculateValorTotal(); // Recalculate when quantity changes
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calculateValorTotal(); // Recalculate when price changes
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    @Override
    public String toString() {
        return "InventoryMovementDTO{" +
                "movimientoId=" + movimientoId +
                ", codigoMovimiento='" + codigoMovimiento + '\'' +
                ", tipo='" + tipo + '\'' +
                ", productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", loteId=" + loteId +
                ", cantidad=" + cantidad +
                ", precioUnitario=" + precioUnitario +
                ", valorTotal=" + valorTotal +
                ", descripcion='" + descripcion + '\'' +
                ", fechaMovimiento=" + fechaMovimiento +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                ", ordenCompraId=" + ordenCompraId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryMovementDTO that = (InventoryMovementDTO) o;
        return movimientoId != null ? movimientoId.equals(that.movimientoId) : that.movimientoId == null;
    }

    @Override
    public int hashCode() {
        return movimientoId != null ? movimientoId.hashCode() : 0;
    }
}