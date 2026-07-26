package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Role information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 9.1-9.6, 14.1-14.10
 */
public class RoleDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Role identifier (auto-generated).
     */
    private Long rolId;

    /**
     * Role name.
     * Required field with size validation.
     * Must be unique across the system.
     */
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 1, max = 50, message = "El nombre del rol debe tener entre 1 y 50 caracteres")
    private String nombre;

    /**
     * Role description.
     * Required field explaining the role's purpose and permissions.
     */
    @NotBlank(message = "La descripción del rol es obligatoria")
    @Size(min = 1, max = 300, message = "La descripción debe tener entre 1 y 300 caracteres")
    private String descripcion;

    /**
     * Whether this role is a system role (non-deletable).
     * System roles like ADMIN, EMPLEADO cannot be deleted.
     */
    private Boolean sistemaRole;

    /**
     * Whether this role is active.
     */
    private Boolean activo;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created the role (read-only).
     */
    private String usuarioCreacion;

    /**
     * Default constructor.
     */
    public RoleDTO() {
        this.activo = true;
        this.sistemaRole = false;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param nombre the role name
     * @param descripcion the role description
     */
    public RoleDTO(String nombre, String descripcion) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Constructor for system roles.
     * 
     * @param nombre the role name
     * @param descripcion the role description
     * @param sistemaRole whether this is a system role
     */
    public RoleDTO(String nombre, String descripcion, boolean sistemaRole) {
        this(nombre, descripcion);
        this.sistemaRole = sistemaRole;
    }

    /**
     * Checks if this role can be deleted.
     * System roles cannot be deleted.
     * 
     * @return true if role can be deleted
     */
    public boolean isDeletable() {
        return !Boolean.TRUE.equals(sistemaRole);
    }

    /**
     * Checks if this role can be modified.
     * System roles have limited modification capabilities.
     * 
     * @return true if role can be modified
     */
    public boolean isModifiable() {
        // System roles can have their description updated but not name
        return !Boolean.TRUE.equals(sistemaRole);
    }

    /**
     * Gets normalized role name (uppercase, no spaces).
     * Used for permission checking and role comparison.
     * 
     * @return normalized role name
     */
    public String getNormalizedNombre() {
        if (nombre == null) {
            return null;
        }
        return nombre.toUpperCase().replaceAll("\\s+", "_");
    }

    /**
     * Creates a system role (predefined roles).
     * 
     * @param nombre the role name
     * @param descripcion the role description
     * @return system role DTO
     */
    public static RoleDTO createSystemRole(String nombre, String descripcion) {
        return new RoleDTO(nombre, descripcion, true);
    }

    /**
     * Creates a custom user-defined role.
     * 
     * @param nombre the role name
     * @param descripcion the role description
     * @return custom role DTO
     */
    public static RoleDTO createCustomRole(String nombre, String descripcion) {
        return new RoleDTO(nombre, descripcion, false);
    }

    // Getters and Setters

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getSistemaRole() {
        return sistemaRole;
    }

    public void setSistemaRole(Boolean sistemaRole) {
        this.sistemaRole = sistemaRole;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
        return "RoleDTO{" +
                "rolId=" + rolId +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", sistemaRole=" + sistemaRole +
                ", activo=" + activo +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleDTO roleDTO = (RoleDTO) o;
        return rolId != null ? rolId.equals(roleDTO.rolId) : roleDTO.rolId == null;
    }

    @Override
    public int hashCode() {
        return rolId != null ? rolId.hashCode() : 0;
    }
}