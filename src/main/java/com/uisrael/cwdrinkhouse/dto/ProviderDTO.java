package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Provider information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 4.7, 14.1-14.10
 */
public class ProviderDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Provider identifier (auto-generated).
     */
    private Long proveedorId;

    /**
     * RUC (tax identification number).
     * Must be exactly 13 digits.
     */
    @NotBlank(message = "El RUC es obligatorio")
    @Size(min = 13, max = 13, message = "El RUC debe tener exactamente 13 dígitos")
    @Pattern(regexp = "\\d{13}", message = "El RUC debe contener solo dígitos")
    private String ruc;

    /**
     * Company legal name.
     * Required field.
     */
    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 1, max = 200, message = "La razón social debe tener entre 1 y 200 caracteres")
    private String razonSocial;

    /**
     * Company address.
     * Required field.
     */
    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 1, max = 300, message = "La dirección debe tener entre 1 y 300 caracteres")
    private String direccion;

    /**
     * Contact phone number.
     * Optional field with pattern validation.
     */
    @Pattern(regexp = "^[+]?[0-9\\-\\s\\(\\)]{7,20}$", 
             message = "El teléfono debe tener un formato válido (7-20 caracteres, solo números, espacios, guiones y paréntesis)")
    private String telefono;

    /**
     * Contact email address.
     * Required field with email format validation.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no debe exceder los 100 caracteres")
    private String email;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created the provider (read-only).
     */
    private String usuarioCreacion;

    /**
     * Whether this provider is active.
     */
    private Boolean activo;

    /**
     * Default constructor.
     */
    public ProviderDTO() {
        this.activo = true;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param ruc the RUC number
     * @param razonSocial the company legal name
     * @param direccion the company address
     * @param email the contact email
     */
    public ProviderDTO(String ruc, String razonSocial, String direccion, String email) {
        this();
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.direccion = direccion;
        this.email = email;
    }

    /**
     * Validates RUC format and checksum (Ecuador specific validation).
     * This could be enhanced with Ecuador RUC validation algorithm.
     * 
     * @return true if RUC format is valid
     */
    public boolean isValidRucFormat() {
        if (ruc == null || ruc.length() != 13) {
            return false;
        }
        return ruc.matches("\\d{13}");
    }

    // Getters and Setters

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    @Override
    public String toString() {
        return "ProviderDTO{" +
                "proveedorId=" + proveedorId +
                ", ruc='" + ruc + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", direccion='" + direccion + '\'' +
                ", telefono='" + telefono + '\'' +
                ", email='" + email + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProviderDTO that = (ProviderDTO) o;
        return proveedorId != null ? proveedorId.equals(that.proveedorId) : that.proveedorId == null;
    }

    @Override
    public int hashCode() {
        return proveedorId != null ? proveedorId.hashCode() : 0;
    }
}