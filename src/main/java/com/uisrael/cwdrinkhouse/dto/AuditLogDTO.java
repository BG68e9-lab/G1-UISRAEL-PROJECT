package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for Audit Log information.
 * Used for displaying audit trail and communication with backend API.
 * 
 * Requirements: 11.1-11.7, 14.1-14.10
 */
public class AuditLogDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Audit log identifier (auto-generated).
     */
    private Long logId;

    /**
     * Entity type that was audited.
     * Examples: CATEGORIA, PRODUCTO, PROVEEDOR, ORDEN_COMPRA, USUARIO, etc.
     */
    @NotBlank(message = "El tipo de entidad es obligatorio")
    @Size(max = 50, message = "El tipo de entidad no debe exceder los 50 caracteres")
    private String entidad;

    /**
     * Entity identifier that was audited.
     */
    @NotNull(message = "El ID de entidad es obligatorio")
    private Long entidadId;

    /**
     * Action performed on the entity.
     * Valid values: CREAR, ACTUALIZAR, ELIMINAR, LEER
     */
    @NotBlank(message = "La acción es obligatoria")
    @Pattern(regexp = "^(CREAR|ACTUALIZAR|ELIMINAR|LEER)$", 
             message = "La acción debe ser CREAR, ACTUALIZAR, ELIMINAR o LEER")
    private String accion;

    /**
     * User ID who performed the action.
     */
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    /**
     * User email who performed the action (for display).
     */
    private String usuarioEmail;

    /**
     * User full name who performed the action (for display).
     */
    private String usuarioNombre;

    /**
     * Timestamp when the action occurred.
     */
    @NotNull(message = "La fecha y hora son obligatorias")
    private LocalDateTime fechaHora;

    /**
     * IP address from where the action was performed.
     */
    @Size(max = 45, message = "La dirección IP no debe exceder los 45 caracteres") // IPv6 support
    private String direccionIp;

    /**
     * User agent (browser/client information).
     */
    @Size(max = 500, message = "El user agent no debe exceder los 500 caracteres")
    private String userAgent;

    /**
     * Entity state before the action (JSON format).
     * Only for ACTUALIZAR and ELIMINAR actions.
     */
    private String estadoAnterior;

    /**
     * Entity state after the action (JSON format).
     * Only for CREAR and ACTUALIZAR actions.
     */
    private String estadoPosterior;

    /**
     * Changes made to the entity (JSON format with field-level differences).
     * Only for ACTUALIZAR actions.
     */
    private String cambios;

    /**
     * Additional details or comments about the audit event.
     */
    @Size(max = 1000, message = "Los detalles no deben exceder los 1000 caracteres")
    private String detalles;

    /**
     * Business/organization ID.
     * Links audit log to specific business context.
     */
    private Long negocioId;

    /**
     * Session ID when the action was performed.
     */
    @Size(max = 100, message = "El ID de sesión no debe exceder los 100 caracteres")
    private String sessionId;

    /**
     * Whether this is a system-generated audit log.
     */
    private Boolean sistemaGenerado;

    /**
     * Default constructor.
     */
    public AuditLogDTO() {
        this.fechaHora = LocalDateTime.now();
        this.sistemaGenerado = false;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param entidad the entity type
     * @param entidadId the entity ID
     * @param accion the action performed
     * @param usuarioId the user ID
     */
    public AuditLogDTO(String entidad, Long entidadId, String accion, Long usuarioId) {
        this();
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.accion = accion;
        this.usuarioId = usuarioId;
    }

    /**
     * Constructor with user context.
     * 
     * @param entidad the entity type
     * @param entidadId the entity ID
     * @param accion the action performed
     * @param usuarioId the user ID
     * @param usuarioEmail the user email
     * @param direccionIp the IP address
     */
    public AuditLogDTO(String entidad, Long entidadId, String accion, Long usuarioId, 
                      String usuarioEmail, String direccionIp) {
        this(entidad, entidadId, accion, usuarioId);
        this.usuarioEmail = usuarioEmail;
        this.direccionIp = direccionIp;
    }

    /**
     * Gets formatted action description for display.
     * 
     * @return formatted action description
     */
    public String getAccionDescripcion() {
        if (accion == null) {
            return "Acción desconocida";
        }
        
        return switch (accion) {
            case "CREAR" -> "Creó";
            case "ACTUALIZAR" -> "Actualizó";
            case "ELIMINAR" -> "Eliminó";
            case "LEER" -> "Consultó";
            default -> accion;
        };
    }

    /**
     * Gets CSS class for action styling.
     * 
     * @return CSS class name
     */
    public String getAccionCssClass() {
        if (accion == null) {
            return "badge-secondary";
        }
        
        return switch (accion) {
            case "CREAR" -> "badge-success";
            case "ACTUALIZAR" -> "badge-warning";
            case "ELIMINAR" -> "badge-danger";
            case "LEER" -> "badge-info";
            default -> "badge-secondary";
        };
    }

    /**
     * Gets icon class for action.
     * 
     * @return icon class name
     */
    public String getAccionIconClass() {
        if (accion == null) {
            return "fas fa-question-circle";
        }
        
        return switch (accion) {
            case "CREAR" -> "fas fa-plus-circle";
            case "ACTUALIZAR" -> "fas fa-edit";
            case "ELIMINAR" -> "fas fa-trash-alt";
            case "LEER" -> "fas fa-eye";
            default -> "fas fa-question-circle";
        };
    }

    /**
     * Checks if this audit entry has change details.
     * 
     * @return true if has change details
     */
    public boolean hasChanges() {
        return cambios != null && !cambios.trim().isEmpty() && !"{}".equals(cambios.trim());
    }

    /**
     * Checks if this audit entry has before state.
     * 
     * @return true if has before state
     */
    public boolean hasEstadoAnterior() {
        return estadoAnterior != null && !estadoAnterior.trim().isEmpty() && !"{}".equals(estadoAnterior.trim());
    }

    /**
     * Checks if this audit entry has after state.
     * 
     * @return true if has after state
     */
    public boolean hasEstadoPosterior() {
        return estadoPosterior != null && !estadoPosterior.trim().isEmpty() && !"{}".equals(estadoPosterior.trim());
    }

    /**
     * Creates audit log for entity creation.
     * 
     * @param entidad the entity type
     * @param entidadId the entity ID
     * @param usuarioId the user ID
     * @param estadoPosterior the created entity state
     * @return audit log DTO for creation
     */
    public static AuditLogDTO forCreation(String entidad, Long entidadId, Long usuarioId, String estadoPosterior) {
        AuditLogDTO audit = new AuditLogDTO(entidad, entidadId, "CREAR", usuarioId);
        audit.setEstadoPosterior(estadoPosterior);
        return audit;
    }

    /**
     * Creates audit log for entity update.
     * 
     * @param entidad the entity type
     * @param entidadId the entity ID
     * @param usuarioId the user ID
     * @param estadoAnterior the previous entity state
     * @param estadoPosterior the updated entity state
     * @param cambios the changes made
     * @return audit log DTO for update
     */
    public static AuditLogDTO forUpdate(String entidad, Long entidadId, Long usuarioId, 
                                       String estadoAnterior, String estadoPosterior, String cambios) {
        AuditLogDTO audit = new AuditLogDTO(entidad, entidadId, "ACTUALIZAR", usuarioId);
        audit.setEstadoAnterior(estadoAnterior);
        audit.setEstadoPosterior(estadoPosterior);
        audit.setCambios(cambios);
        return audit;
    }

    /**
     * Creates audit log for entity deletion.
     * 
     * @param entidad the entity type
     * @param entidadId the entity ID
     * @param usuarioId the user ID
     * @param estadoAnterior the deleted entity state
     * @return audit log DTO for deletion
     */
    public static AuditLogDTO forDeletion(String entidad, Long entidadId, Long usuarioId, String estadoAnterior) {
        AuditLogDTO audit = new AuditLogDTO(entidad, entidadId, "ELIMINAR", usuarioId);
        audit.setEstadoAnterior(estadoAnterior);
        return audit;
    }

    // Getters and Setters

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioEmail() {
        return usuarioEmail;
    }

    public void setUsuarioEmail(String usuarioEmail) {
        this.usuarioEmail = usuarioEmail;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoPosterior() {
        return estadoPosterior;
    }

    public void setEstadoPosterior(String estadoPosterior) {
        this.estadoPosterior = estadoPosterior;
    }

    public String getCambios() {
        return cambios;
    }

    public void setCambios(String cambios) {
        this.cambios = cambios;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getSistemaGenerado() {
        return sistemaGenerado;
    }

    public void setSistemaGenerado(Boolean sistemaGenerado) {
        this.sistemaGenerado = sistemaGenerado;
    }

    @Override
    public String toString() {
        return "AuditLogDTO{" +
                "logId=" + logId +
                ", entidad='" + entidad + '\'' +
                ", entidadId=" + entidadId +
                ", accion='" + accion + '\'' +
                ", usuarioId=" + usuarioId +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", usuarioNombre='" + usuarioNombre + '\'' +
                ", fechaHora=" + fechaHora +
                ", direccionIp='" + direccionIp + '\'' +
                ", negocioId=" + negocioId +
                ", sistemaGenerado=" + sistemaGenerado +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuditLogDTO that = (AuditLogDTO) o;
        return logId != null ? logId.equals(that.logId) : that.logId == null;
    }

    @Override
    public int hashCode() {
        return logId != null ? logId.hashCode() : 0;
    }
}