package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Product Lot information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 6.1-6.7, 14.1-14.10
 */
public class LotDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Lot identifier (auto-generated).
     */
    private Long loteId;

    /**
     * Entry code for the lot.
     * Auto-generated with format: LOT-YYYYMMDD-HHMMSS
     */
    private String codigoEntrada;

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
     * Initial quantity when lot was created.
     * Must be a positive integer.
     */
    @NotNull(message = "La cantidad inicial es obligatoria")
    @Positive(message = "La cantidad inicial debe ser un número positivo")
    @Max(value = 999999, message = "La cantidad inicial no puede ser mayor a 999,999")
    private Integer cantidadInicial;

    /**
     * Current available quantity.
     * Updated automatically with inventory movements.
     */
    @NotNull(message = "La cantidad disponible es obligatoria")
    @Min(value = 0, message = "La cantidad disponible no puede ser negativa")
    @Max(value = 999999, message = "La cantidad disponible no puede ser mayor a 999,999")
    private Integer cantidadDisponible;

    /**
     * Cost price per unit for this lot.
     * Must be a positive decimal value.
     */
    @NotNull(message = "El precio de costo es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de costo debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "El precio de costo debe tener máximo 2 decimales")
    private BigDecimal precioCosto;

    /**
     * Date when the lot was received/created.
     */
    @NotNull(message = "La fecha de ingreso es obligatoria")
    @PastOrPresent(message = "La fecha de ingreso no puede ser futura")
    private LocalDate fechaIngreso;

    /**
     * Expiration date of the lot.
     * Must be after the ingreso date.
     */
    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Future(message = "La fecha de vencimiento debe ser futura")
    private LocalDate fechaVencimiento;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created the lot (read-only).
     */
    private String usuarioCreacion;

    /**
     * Whether this lot is active.
     */
    private Boolean activo;

    /**
     * Order ID that generated this lot (if applicable).
     */
    private Long ordenCompraId;

    /**
     * Default constructor.
     */
    public LotDTO() {
        this.activo = true;
        this.fechaIngreso = LocalDate.now();
    }

    /**
     * Constructor with essential fields.
     * 
     * @param productoId the product ID
     * @param cantidadInicial the initial quantity
     * @param precioCosto the cost price
     * @param fechaVencimiento the expiration date
     */
    public LotDTO(Long productoId, Integer cantidadInicial, BigDecimal precioCosto, LocalDate fechaVencimiento) {
        this();
        this.productoId = productoId;
        this.cantidadInicial = cantidadInicial;
        this.cantidadDisponible = cantidadInicial; // Initially, all quantity is available
        this.precioCosto = precioCosto;
        this.fechaVencimiento = fechaVencimiento;
    }

    /**
     * Checks if the lot is expiring soon (within 7 days).
     * 
     * @return true if expiring within 7 days
     */
    public boolean isExpiringSoon() {
        if (fechaVencimiento == null) {
            return false;
        }
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        return fechaVencimiento.isBefore(sevenDaysFromNow) || fechaVencimiento.isEqual(sevenDaysFromNow);
    }

    /**
     * Checks if the lot is expired.
     * 
     * @return true if expired
     */
    public boolean isExpired() {
        if (fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isBefore(LocalDate.now());
    }

    /**
     * Gets the days until expiration.
     * 
     * @return days until expiration (negative if expired)
     */
    public long getDaysUntilExpiration() {
        if (fechaVencimiento == null) {
            return Long.MAX_VALUE;
        }
        return LocalDate.now().until(fechaVencimiento).getDays();
    }

    /**
     * Checks if the lot has available stock.
     * 
     * @return true if has available stock
     */
    public boolean hasAvailableStock() {
        return cantidadDisponible != null && cantidadDisponible > 0;
    }

    /**
     * Gets the percentage of stock consumed.
     * 
     * @return percentage consumed (0-100)
     */
    public double getConsumedPercentage() {
        if (cantidadInicial == null || cantidadInicial == 0 || cantidadDisponible == null) {
            return 0.0;
        }
        double consumed = cantidadInicial - cantidadDisponible;
        return (consumed / cantidadInicial) * 100.0;
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

    /**
     * Custom validation for date range.
     * 
     * @return true if dates are valid
     */
    public boolean isValidDateRange() {
        if (fechaIngreso == null || fechaVencimiento == null) {
            return false;
        }
        return fechaVencimiento.isAfter(fechaIngreso);
    }

    // Getters and Setters

    public Long getLoteId() {
        return loteId;
    }

    public void setLoteId(Long loteId) {
        this.loteId = loteId;
    }

    public String getCodigoEntrada() {
        return codigoEntrada;
    }

    public void setCodigoEntrada(String codigoEntrada) {
        this.codigoEntrada = codigoEntrada;
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

    public Integer getCantidadInicial() {
        return cantidadInicial;
    }

    public void setCantidadInicial(Integer cantidadInicial) {
        this.cantidadInicial = cantidadInicial;
    }

    public Integer getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(Integer cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public BigDecimal getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(BigDecimal precioCosto) {
        this.precioCosto = precioCosto;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    @Override
    public String toString() {
        return "LotDTO{" +
                "loteId=" + loteId +
                ", codigoEntrada='" + codigoEntrada + '\'' +
                ", productoId=" + productoId +
                ", productoNombre='" + productoNombre + '\'' +
                ", cantidadInicial=" + cantidadInicial +
                ", cantidadDisponible=" + cantidadDisponible +
                ", precioCosto=" + precioCosto +
                ", fechaIngreso=" + fechaIngreso +
                ", fechaVencimiento=" + fechaVencimiento +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                ", activo=" + activo +
                ", ordenCompraId=" + ordenCompraId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LotDTO lotDTO = (LotDTO) o;
        return loteId != null ? loteId.equals(lotDTO.loteId) : lotDTO.loteId == null;
    }

    @Override
    public int hashCode() {
        return loteId != null ? loteId.hashCode() : 0;
    }
}