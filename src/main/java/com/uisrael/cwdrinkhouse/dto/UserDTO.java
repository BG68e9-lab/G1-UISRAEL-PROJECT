package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for User information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 8.1-8.13, 14.1-14.10
 */
public class UserDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * User identifier (auto-generated).
     */
    private Long usuarioId;

    /**
     * User first names.
     * Required field.
     */
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 1, max = 100, message = "Los nombres deben tener entre 1 y 100 caracteres")
    private String nombres;

    /**
     * User last names.
     * Required field.
     */
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 1, max = 100, message = "Los apellidos deben tener entre 1 y 100 caracteres")
    private String apellidos;

    /**
     * User email address.
     * Required field with email format validation.
     */
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no debe exceder los 150 caracteres")
    private String email;

    /**
     * Authentication method.
     * Valid values: LOCAL, SSO
     */
    @NotBlank(message = "El método de autenticación es obligatorio")
    @Pattern(regexp = "^(LOCAL|SSO)$", 
             message = "El método de autenticación debe ser LOCAL o SSO")
    private String metodoAutenticacion;

    /**
     * Password hash for local authentication.
     * Required only when metodoAutenticacion is LOCAL.
     * Minimum 8 characters when provided.
     */
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres", groups = LocalAuthValidation.class)
    private String passwordHash;

    /**
     * SSO provider name.
     * Required only when metodoAutenticacion is SSO.
     */
    @Size(max = 50, message = "El proveedor SSO no debe exceder los 50 caracteres")
    private String ssoProvider;

    /**
     * SSO subject identifier.
     * Required only when metodoAutenticacion is SSO.
     */
    @Size(max = 200, message = "El identificador SSO no debe exceder los 200 caracteres")
    private String ssoSubjectId;

    /**
     * Account status.
     * Valid values: PENDIENTE, ACTIVO, INACTIVO, BLOQUEADO
     */
    @NotBlank(message = "El estado de cuenta es obligatorio")
    @Pattern(regexp = "^(PENDIENTE|ACTIVO|INACTIVO|BLOQUEADO)$", 
             message = "El estado debe ser PENDIENTE, ACTIVO, INACTIVO o BLOQUEADO")
    private String estadoCuenta;

    /**
     * User roles.
     * Collection of role names assigned to the user.
     */
    private Set<String> roles;

    /**
     * Business/organization ID.
     * Links user to specific business context.
     */
    private Long negocioId;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created this user (read-only).
     */
    private String usuarioCreacion;

    /**
     * Last login timestamp (read-only).
     */
    private LocalDateTime ultimoLogin;

    /**
     * Whether user account is enabled.
     */
    private Boolean habilitado;

    /**
     * Validation group for LOCAL authentication specific fields.
     */
    public interface LocalAuthValidation {
    }

    /**
     * Validation group for SSO authentication specific fields.
     */
    public interface SsoAuthValidation {
    }

    /**
     * Default constructor.
     */
    public UserDTO() {
        this.estadoCuenta = "PENDIENTE";
        this.metodoAutenticacion = "LOCAL";
        this.habilitado = true;
        this.roles = new HashSet<>();
    }

    /**
     * Constructor for local authentication user.
     * 
     * @param nombres the first names
     * @param apellidos the last names
     * @param email the email address
     * @param passwordHash the password hash
     */
    public UserDTO(String nombres, String apellidos, String email, String passwordHash) {
        this();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.metodoAutenticacion = "LOCAL";
        this.passwordHash = passwordHash;
    }

    /**
     * Constructor for SSO authentication user.
     * 
     * @param nombres the first names
     * @param apellidos the last names
     * @param email the email address
     * @param ssoProvider the SSO provider
     * @param ssoSubjectId the SSO subject ID
     */
    public UserDTO(String nombres, String apellidos, String email, String ssoProvider, String ssoSubjectId) {
        this();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.metodoAutenticacion = "SSO";
        this.ssoProvider = ssoProvider;
        this.ssoSubjectId = ssoSubjectId;
    }

    /**
     * Validates conditional authentication fields.
     * 
     * @return true if validation passes, false otherwise
     */
    public boolean isValidAuthentication() {
        if (metodoAutenticacion == null) {
            return false;
        }

        return switch (metodoAutenticacion) {
            case "LOCAL" -> validateLocalAuth();
            case "SSO" -> validateSsoAuth();
            default -> false;
        };
    }

    /**
     * Validates LOCAL authentication requirements.
     * - Must have passwordHash with minimum 8 characters
     */
    private boolean validateLocalAuth() {
        return passwordHash != null && passwordHash.length() >= 8;
    }

    /**
     * Validates SSO authentication requirements.
     * - Must have ssoProvider and ssoSubjectId
     */
    private boolean validateSsoAuth() {
        return ssoProvider != null && !ssoProvider.trim().isEmpty() &&
               ssoSubjectId != null && !ssoSubjectId.trim().isEmpty();
    }

    /**
     * Gets validation error message for authentication if invalid.
     * 
     * @return error message or null if valid
     */
    public String getAuthenticationValidationError() {
        if (metodoAutenticacion == null) {
            return "El método de autenticación es obligatorio";
        }

        return switch (metodoAutenticacion) {
            case "LOCAL" -> {
                if (passwordHash == null || passwordHash.length() < 8) {
                    yield "La contraseña es obligatoria y debe tener al menos 8 caracteres para autenticación local";
                }
                yield null;
            }
            case "SSO" -> {
                if (ssoProvider == null || ssoProvider.trim().isEmpty()) {
                    yield "El proveedor SSO es obligatorio para autenticación SSO";
                }
                if (ssoSubjectId == null || ssoSubjectId.trim().isEmpty()) {
                    yield "El identificador SSO es obligatorio para autenticación SSO";
                }
                yield null;
            }
            default -> "Método de autenticación no válido";
        };
    }

    /**
     * Gets full name (nombres + apellidos).
     * 
     * @return formatted full name
     */
    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (nombres != null) {
            sb.append(nombres);
        }
        if (apellidos != null) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(apellidos);
        }
        return sb.toString();
    }

    /**
     * Checks if user has a specific role.
     * 
     * @param role the role name to check
     * @return true if user has the role
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Adds a role to the user.
     * 
     * @param role the role name to add
     */
    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    /**
     * Removes a role from the user.
     * 
     * @param role the role name to remove
     */
    public void removeRole(String role) {
        if (roles != null) {
            roles.remove(role);
        }
    }

    /**
     * Checks if user account is active and enabled.
     * 
     * @return true if account is active and enabled
     */
    public boolean isAccountActive() {
        return "ACTIVO".equals(estadoCuenta) && Boolean.TRUE.equals(habilitado);
    }

    /**
     * Checks if user account can be activated (from PENDIENTE state).
     * 
     * @return true if account can be activated
     */
    public boolean canActivate() {
        return "PENDIENTE".equals(estadoCuenta);
    }

    /**
     * Checks if user account can be deactivated (from ACTIVO state).
     * 
     * @return true if account can be deactivated
     */
    public boolean canDeactivate() {
        return "ACTIVO".equals(estadoCuenta);
    }

    // Getters and Setters

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMetodoAutenticacion() {
        return metodoAutenticacion;
    }

    public void setMetodoAutenticacion(String metodoAutenticacion) {
        this.metodoAutenticacion = metodoAutenticacion;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSsoProvider() {
        return ssoProvider;
    }

    public void setSsoProvider(String ssoProvider) {
        this.ssoProvider = ssoProvider;
    }

    public String getSsoSubjectId() {
        return ssoSubjectId;
    }

    public void setSsoSubjectId(String ssoSubjectId) {
        this.ssoSubjectId = ssoSubjectId;
    }

    public String getEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(String estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
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

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Boolean getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Boolean habilitado) {
        this.habilitado = habilitado;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "usuarioId=" + usuarioId +
                ", nombres='" + nombres + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", email='" + email + '\'' +
                ", metodoAutenticacion='" + metodoAutenticacion + '\'' +
                ", ssoProvider='" + ssoProvider + '\'' +
                ", ssoSubjectId='" + ssoSubjectId + '\'' +
                ", estadoCuenta='" + estadoCuenta + '\'' +
                ", roles=" + roles +
                ", negocioId=" + negocioId +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                ", ultimoLogin=" + ultimoLogin +
                ", habilitado=" + habilitado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;
        return usuarioId != null ? usuarioId.equals(userDTO.usuarioId) : userDTO.usuarioId == null;
    }

    @Override
    public int hashCode() {
        return usuarioId != null ? usuarioId.hashCode() : 0;
    }
}