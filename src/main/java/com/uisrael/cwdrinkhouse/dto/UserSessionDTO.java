package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for user session information.
 * 
 * This object is stored in the HTTP session and contains
 * user authentication and authorization information.
 */
public class UserSessionDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String email;
    private String nombreCompleto;
    private String nombreNegocio;
    private Long negocioId;
    private Set<String> roles;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;
    private boolean active;

    /**
     * Default constructor.
     */
    public UserSessionDTO() {
        this.roles = new HashSet<>();
        this.active = true;
        this.loginTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
    }

    /**
     * Constructor with essential user information.
     */
    public UserSessionDTO(Long userId, String email, String nombreCompleto, Long negocioId) {
        this();
        this.userId = userId;
        this.email = email;
        this.nombreCompleto = nombreCompleto;
        this.negocioId = negocioId;
        this.roles = new HashSet<>();
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombreNegocio() {
        return nombreNegocio;
    }

    public void setNombreNegocio(String nombreNegocio) {
        this.nombreNegocio = nombreNegocio;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public boolean hasRole(String role) {
        return this.roles != null && this.roles.contains(role);
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "UserSessionDTO{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", nombreNegocio='" + nombreNegocio + '\'' +
                ", negocioId=" + negocioId +
                ", roles=" + roles +
                ", loginTime=" + loginTime +
                ", active=" + active +
                '}';
    }
}
