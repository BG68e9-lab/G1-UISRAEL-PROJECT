package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Category information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 2.9, 14.1-14.10
 */
public class CategoryDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Category identifier (auto-generated).
     */
    private Long categoriaId;

    /**
     * Category name.
     * Required field with size validation.
     */
    @NotBlank(message = "El nombre de la categoría es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;

    /**
     * Profit margin percentage.
     * Must be a positive number.
     */
    @NotNull(message = "El margen de ganancia es obligatorio")
    @Positive(message = "El margen de ganancia debe ser un número positivo")
    private BigDecimal margenGananciaPct;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created the category (read-only).
     */
    private String usuarioCreacion;

    /**
     * Default constructor.
     */
    public CategoryDTO() {
    }

    /**
     * Constructor with essential fields.
     * 
     * @param nombre the category name
     * @param margenGananciaPct the profit margin percentage
     */
    public CategoryDTO(String nombre, BigDecimal margenGananciaPct) {
        this.nombre = nombre;
        this.margenGananciaPct = margenGananciaPct;
    }

    // Getters and Setters

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getMargenGananciaPct() {
        return margenGananciaPct;
    }

    public void setMargenGananciaPct(BigDecimal margenGananciaPct) {
        this.margenGananciaPct = margenGananciaPct;
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
        return "CategoryDTO{" +
                "categoriaId=" + categoriaId +
                ", nombre='" + nombre + '\'' +
                ", margenGananciaPct=" + margenGananciaPct +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryDTO that = (CategoryDTO) o;
        return categoriaId != null ? categoriaId.equals(that.categoriaId) : that.categoriaId == null;
    }

    @Override
    public int hashCode() {
        return categoriaId != null ? categoriaId.hashCode() : 0;
    }
}