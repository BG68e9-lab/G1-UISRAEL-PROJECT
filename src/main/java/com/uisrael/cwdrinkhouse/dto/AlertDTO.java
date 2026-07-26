package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Alert information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 10.1-10.12, 14.1-14.10
 */
public class AlertDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Alert identifier (auto-generated).
     */
    private Long alertaId;

    /**
     * Alert title.
     * Required field with descriptive message.
     */
    @NotBlank(message = "El título de la alerta es obligatorio")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    private String titulo;

    /**
     * Alert detailed message.
     * Required field with alert description.
     */
    @NotBlank(message = "El mensaje de la alerta es obligatorio")
    @Size(min = 1, max = 1000, message = "El mensaje debe tener entre 1 y 1000 caracteres")
    private String mensaje;

    /**
     * Alert type.
     * Valid values: STOCK_BAJO, VENCIMIENTO_PROXIMO, SISTEMA, USUARIO, NEGOCIO
     */
    @NotBlank(message = "El tipo de alerta es obligatorio")
    @Pattern(regexp = "^(STOCK_BAJO|VENCIMIENTO_PROXIMO|SISTEMA|USUARIO|NEGOCIO)$", 
             message = "El tipo debe ser STOCK_BAJO, VENCIMIENTO_PROXIMO, SISTEMA, USUARIO o NEGOCIO")
    private String tipo;

    /**
     * Alert priority level.
     * Valid values: ALTA, MEDIA, BAJA
     */
    @NotBlank(message = "La prioridad es obligatoria")
    @Pattern(regexp = "^(ALTA|MEDIA|BAJA)$", 
             message = "La prioridad debe ser ALTA, MEDIA o BAJA")
    private String prioridad;

    /**
     * Whether the alert has been read.
     */
    @NotNull(message = "El estado de lectura es obligatorio")
    private Boolean leido;

    /**
     * Entity type related to the alert (if applicable).
     * Examples: PRODUCTO, LOTE, USUARIO, ORDEN_COMPRA
     */
    @Size(max = 50, message = "El tipo de entidad no debe exceder los 50 caracteres")
    private String entidadTipo;

    /**
     * Entity ID related to the alert (if applicable).
     */
    private Long entidadId;

    /**
     * Business/organization ID.
     * Links alert to specific business context.
     */
    private Long negocioId;

    /**
     * Target user ID (for user-specific alerts).
     * If null, alert is for all users.
     */
    private Long usuarioId;

    /**
     * Creation timestamp (auto-generated).
     */
    private LocalDateTime fechaCreacion;

    /**
     * When the alert was read (if applicable).
     */
    private LocalDateTime fechaLeido;

    /**
     * User who read the alert.
     */
    private String usuarioLeido;

    /**
     * Alert expiration date (auto-dismiss).
     */
    private LocalDateTime fechaExpiracion;

    /**
     * Whether this alert is active.
     */
    private Boolean activo;

    /**
     * Default constructor.
     */
    public AlertDTO() {
        this.leido = false;
        this.activo = true;
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Constructor with essential fields.
     * 
     * @param titulo the alert title
     * @param mensaje the alert message
     * @param tipo the alert type
     * @param prioridad the alert priority
     */
    public AlertDTO(String titulo, String mensaje, String tipo, String prioridad) {
        this();
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.prioridad = prioridad;
    }

    /**
     * Constructor for entity-related alerts.
     * 
     * @param titulo the alert title
     * @param mensaje the alert message
     * @param tipo the alert type
     * @param prioridad the alert priority
     * @param entidadTipo the related entity type
     * @param entidadId the related entity ID
     */
    public AlertDTO(String titulo, String mensaje, String tipo, String prioridad, 
                   String entidadTipo, Long entidadId) {
        this(titulo, mensaje, tipo, prioridad);
        this.entidadTipo = entidadTipo;
        this.entidadId = entidadId;
    }

    /**
     * Marks the alert as read by a user.
     * 
     * @param usuarioEmail the email of user who read the alert
     */
    public void markAsRead(String usuarioEmail) {
        this.leido = true;
        this.fechaLeido = LocalDateTime.now();
        this.usuarioLeido = usuarioEmail;
    }

    /**
     * Checks if the alert is expired.
     * 
     * @return true if alert has expired
     */
    public boolean isExpired() {
        return fechaExpiracion != null && LocalDateTime.now().isAfter(fechaExpiracion);
    }

    /**
     * Checks if the alert is unread.
     * 
     * @return true if alert is unread
     */
    public boolean isUnread() {
        return !Boolean.TRUE.equals(leido);
    }

    /**
     * Gets CSS class for alert styling based on type.
     * 
     * @return CSS class name
     */
    public String getCssClass() {
        if (tipo == null) {
            return "alert-info";
        }
        
        return switch (tipo) {
            case "STOCK_BAJO" -> "alert-warning";
            case "VENCIMIENTO_PROXIMO" -> "alert-danger";
            case "SISTEMA" -> "alert-info";
            case "USUARIO" -> "alert-primary";
            case "NEGOCIO" -> "alert-secondary";
            default -> "alert-info";
        };
    }

    /**
     * Gets icon class for alert based on type.
     * 
     * @return icon class name
     */
    public String getIconClass() {
        if (tipo == null) {
            return "fas fa-info-circle";
        }
        
        return switch (tipo) {
            case "STOCK_BAJO" -> "fas fa-exclamation-triangle";
            case "VENCIMIENTO_PROXIMO" -> "fas fa-clock";
            case "SISTEMA" -> "fas fa-cogs";
            case "USUARIO" -> "fas fa-user";
            case "NEGOCIO" -> "fas fa-building";
            default -> "fas fa-info-circle";
        };
    }

    /**
     * Gets priority order (lower number = higher priority).
     * 
     * @return priority order
     */
    public int getPriorityOrder() {
        if (prioridad == null) {
            return 2; // Default to MEDIA
        }
        
        return switch (prioridad) {
            case "ALTA" -> 0;
            case "MEDIA" -> 1;
            case "BAJA" -> 2;
            default -> 2;
        };
    }

    /**
     * Creates a low stock alert.
     * 
     * @param productoNombre the product name
     * @param cantidadDisponible the available quantity
     * @param productoId the product ID
     * @param negocioId the business ID
     * @return low stock alert DTO
     */
    public static AlertDTO createStockBajoAlert(String productoNombre, int cantidadDisponible, 
                                               Long productoId, Long negocioId) {
        String titulo = "Stock bajo: " + productoNombre;
        String mensaje = String.format("El producto %s tiene solo %d unidades disponibles", 
                                     productoNombre, cantidadDisponible);
        
        AlertDTO alert = new AlertDTO(titulo, mensaje, "STOCK_BAJO", "MEDIA", "PRODUCTO", productoId);
        alert.setNegocioId(negocioId);
        return alert;
    }

    /**
     * Creates an expiration alert.
     * 
     * @param productoNombre the product name
     * @param loteId the lot ID
     * @param diasVencimiento days until expiration
     * @param negocioId the business ID
     * @return expiration alert DTO
     */
    public static AlertDTO createVencimientoAlert(String productoNombre, Long loteId, 
                                                 long diasVencimiento, Long negocioId) {
        String titulo = "Producto próximo a vencer: " + productoNombre;
        String mensaje = String.format("El lote del producto %s vence en %d días", 
                                     productoNombre, diasVencimiento);
        
        AlertDTO alert = new AlertDTO(titulo, mensaje, "VENCIMIENTO_PROXIMO", "ALTA", "LOTE", loteId);
        alert.setNegocioId(negocioId);
        return alert;
    }

    // Getters and Setters

    public Long getAlertaId() {
        return alertaId;
    }

    public void setAlertaId(Long alertaId) {
        this.alertaId = alertaId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public Boolean getLeido() {
        return leido;
    }

    public void setLeido(Boolean leido) {
        this.leido = leido;
    }

    public String getEntidadTipo() {
        return entidadTipo;
    }

    public void setEntidadTipo(String entidadTipo) {
        this.entidadTipo = entidadTipo;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaLeido() {
        return fechaLeido;
    }

    public void setFechaLeido(LocalDateTime fechaLeido) {
        this.fechaLeido = fechaLeido;
    }

    public String getUsuarioLeido() {
        return usuarioLeido;
    }

    public void setUsuarioLeido(String usuarioLeido) {
        this.usuarioLeido = usuarioLeido;
    }

    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }

    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return "AlertDTO{" +
                "alertaId=" + alertaId +
                ", titulo='" + titulo + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", tipo='" + tipo + '\'' +
                ", prioridad='" + prioridad + '\'' +
                ", leido=" + leido +
                ", entidadTipo='" + entidadTipo + '\'' +
                ", entidadId=" + entidadId +
                ", negocioId=" + negocioId +
                ", usuarioId=" + usuarioId +
                ", fechaCreacion=" + fechaCreacion +
                ", fechaLeido=" + fechaLeido +
                ", usuarioLeido='" + usuarioLeido + '\'' +
                ", fechaExpiracion=" + fechaExpiracion +
                ", activo=" + activo +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertDTO alertDTO = (AlertDTO) o;
        return alertaId != null ? alertaId.equals(alertDTO.alertaId) : alertDTO.alertaId == null;
    }

    @Override
    public int hashCode() {
        return alertaId != null ? alertaId.hashCode() : 0;
    }
}