package com.uisrael.cwdrinkhouse.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Product information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 3.10, 14.1-14.10
 */
public class ProductDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Product identifier (auto-generated).
     */
    @JsonAlias({"id", "producto_id"})
    private Long productoId;

    /**
     * Product name.
     * Required field with size validation.
     */
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 1, max = 200, message = "El nombre debe tener entre 1 y 200 caracteres")
    private String nombre;

    /**
     * Product brand.
     * Required field.
     */
    @NotBlank(message = "La marca del producto es obligatoria")
    @Size(max = 100, message = "La marca no debe exceder los 100 caracteres")
    private String marca;

    /**
     * Product type.
     * Required field.
     */
    @NotBlank(message = "El tipo de producto es obligatorio")
    @Size(max = 50, message = "El tipo no debe exceder los 50 caracteres")
    private String tipo;

    /**
     * Category identifier.
     * Required field linking to category.
     */
    @NotNull(message = "La categoría es obligatoria")
    @JsonAlias({"categoria_id", "categoriaId", "idCategoria"})
    private Long categoriaId;

    /**
     * Category name (read-only, resolved from categoriaId).
     */
    private String categoriaNombre;

    /**
     * Average cost of the product.
     * Must be a positive decimal value.
     */
    @NotNull(message = "El costo promedio es obligatorio")
    @DecimalMin(value = "0.01", message = "El costo promedio debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "El costo promedio debe tener máximo 2 decimales")
    private BigDecimal costoPromedio;

    /**
     * Profit margin for the product.
     * Must be a positive decimal value.
     */
    @NotNull(message = "El margen de ganancia es obligatorio")
    @DecimalMin(value = "0.01", message = "El margen de ganancia debe ser mayor a cero")
    @Digits(integer = 5, fraction = 2, message = "El margen de ganancia debe tener máximo 2 decimales")
    private BigDecimal margenGanancia;

    /**
     * Whether the product uses a custom sale price.
     * When true, precioVenta is required and editable.
     */
    @NotNull(message = "Debe especificar si usa precio personalizado")
    private Boolean precioPersonalizado;

    /**
     * Custom sale price.
     * Required only when precioPersonalizado is true.
     * Note: Conditional validation is handled at the service/controller level
     */
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a cero", groups = ConditionalValidation.class)
    @Digits(integer = 10, fraction = 2, message = "El precio de venta debe tener máximo 2 decimales")
    private BigDecimal precioVenta;

    /**
     * Product barcode.
     * Optional field with pattern validation.
     */
    @Size(max = 50, message = "El código de barras no debe exceder los 50 caracteres")
    @Pattern(regexp = "^[0-9]*$", message = "El código de barras solo puede contener números")
    private String codigoBarras;

    /**
     * Creation timestamp (read-only).
     */
    @JsonAlias({"creadoEn", "createdAt", "created_at", "fechaCreacion"})
    private LocalDateTime fechaCreacion;

    /**
     * User who created the product (read-only).
     */
    private String usuarioCreacion;

    /**
     * Validation group for conditional fields.
     */
    public interface ConditionalValidation {
    }

    /**
     * Default constructor.
     */
    public ProductDTO() {
        this.precioPersonalizado = false;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param nombre the product name
     * @param marca the product brand
     * @param tipo the product type
     * @param categoriaId the category ID
     * @param costoPromedio the average cost
     * @param margenGanancia the profit margin
     */
    public ProductDTO(String nombre, String marca, String tipo, Long categoriaId, 
                     BigDecimal costoPromedio, BigDecimal margenGanancia) {
        this();
        this.nombre = nombre;
        this.marca = marca;
        this.tipo = tipo;
        this.categoriaId = categoriaId;
        this.costoPromedio = costoPromedio;
        this.margenGanancia = margenGanancia;
    }

    /**
     * Validates that precioVenta is provided when precioPersonalizado is true.
     * This method should be called in controllers for conditional validation.
     * 
     * @return true if validation passes, false otherwise
     */
    public boolean isValidPrecioVenta() {
        if (Boolean.TRUE.equals(precioPersonalizado)) {
            return precioVenta != null && precioVenta.compareTo(BigDecimal.ZERO) > 0;
        }
        return true;
    }

    /**
     * Get validation error message for precio venta if invalid.
     * 
     * @return error message or null if valid
     */
    public String getPrecioVentaValidationError() {
        if (Boolean.TRUE.equals(precioPersonalizado) && 
            (precioVenta == null || precioVenta.compareTo(BigDecimal.ZERO) <= 0)) {
            return "El precio de venta es obligatorio cuando se usa precio personalizado";
        }
        return null;
    }

    // Getters and Setters

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
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

    public Boolean getPrecioPersonalizado() {
        return precioPersonalizado;
    }

    public void setPrecioPersonalizado(Boolean precioPersonalizado) {
        this.precioPersonalizado = precioPersonalizado;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productoId=" + productoId +
                ", nombre='" + nombre + '\'' +
                ", marca='" + marca + '\'' +
                ", tipo='" + tipo + '\'' +
                ", categoriaId=" + categoriaId +
                ", costoPromedio=" + costoPromedio +
                ", margenGanancia=" + margenGanancia +
                ", precioPersonalizado=" + precioPersonalizado +
                ", precioVenta=" + precioVenta +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDTO that = (ProductDTO) o;
        return productoId != null ? productoId.equals(that.productoId) : that.productoId == null;
    }

    @Override
    public int hashCode() {
        return productoId != null ? productoId.hashCode() : 0;
    }
}