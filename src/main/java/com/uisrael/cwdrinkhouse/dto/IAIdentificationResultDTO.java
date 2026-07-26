package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for AI Product Identification results.
 * Used for displaying AI identification results and communication with backend API.
 * 
 * Requirements: 12.1-12.10, 14.1-14.10
 */
public class IAIdentificationResultDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Identification result identifier (auto-generated).
     */
    private Long identificacionId;

    /**
     * Base64 encoded image that was analyzed.
     * This field is used for input validation.
     */
    @NotBlank(message = "La imagen es obligatoria para la identificación")
    @Size(min = 100, message = "La imagen debe ser válida (mínimo 100 caracteres en base64)")
    private String imagenBase64;

    /**
     * Confidence level of the identification.
     * Valid values: ALTA (>= 80%), MEDIA (50-79%), BAJA (< 50%)
     */
    @NotBlank(message = "El nivel de confianza es obligatorio")
    @Pattern(regexp = "^(ALTA|MEDIA|BAJA)$", 
             message = "El nivel de confianza debe ser ALTA, MEDIA o BAJA")
    private String nivelConfianza;

    /**
     * Confidence percentage (0-100).
     */
    @NotNull(message = "El porcentaje de confianza es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje de confianza no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El porcentaje de confianza no puede ser mayor a 100")
    @Digits(integer = 3, fraction = 2, message = "El porcentaje debe tener máximo 2 decimales")
    private BigDecimal porcentajeConfianza;

    /**
     * Identified product name.
     */
    @Size(max = 200, message = "El nombre del producto no debe exceder los 200 caracteres")
    private String productoNombre;

    /**
     * Identified product brand.
     */
    @Size(max = 100, message = "La marca del producto no debe exceder los 100 caracteres")
    private String productoMarca;

    /**
     * Identified product type.
     */
    @Size(max = 50, message = "El tipo del producto no debe exceder los 50 caracteres")
    private String productoTipo;

    /**
     * Identified barcode (if detected).
     */
    @Size(max = 50, message = "El código de barras no debe exceder los 50 caracteres")
    private String codigoBarras;

    /**
     * Suggested category based on AI analysis.
     */
    @Size(max = 100, message = "La categoría sugerida no debe exceder los 100 caracteres")
    private String categoriaSugerida;

    /**
     * Additional attributes detected by AI (JSON format).
     * Examples: color, size, volume, alcohol content, etc.
     */
    private String atributosDetectados;

    /**
     * Alternative identification results (lower confidence).
     */
    private List<String> alternativasSugeridas;

    /**
     * Processing time in milliseconds.
     */
    private Long tiempoProcesamiento;

    /**
     * AI model version used for identification.
     */
    @Size(max = 50, message = "La versión del modelo no debe exceder los 50 caracteres")
    private String modeloVersion;

    /**
     * Processing timestamp.
     */
    private LocalDateTime fechaProcesamiento;

    /**
     * User who requested the identification.
     */
    private Long usuarioId;

    /**
     * User email who requested the identification (for display).
     */
    private String usuarioEmail;

    /**
     * Business/organization ID.
     */
    private Long negocioId;

    /**
     * Whether the result was accepted and used by the user.
     */
    private Boolean resultadoAceptado;

    /**
     * Timestamp when result was accepted/used.
     */
    private LocalDateTime fechaAceptacion;

    /**
     * Error message if identification failed.
     */
    @Size(max = 500, message = "El mensaje de error no debe exceder los 500 caracteres")
    private String mensajeError;

    /**
     * Whether this identification consumed from monthly quota.
     */
    private Boolean consumoQuota;

    /**
     * Default constructor.
     */
    public IAIdentificationResultDTO() {
        this.fechaProcesamiento = LocalDateTime.now();
        this.resultadoAceptado = false;
        this.consumoQuota = true;
        this.alternativasSugeridas = new ArrayList<>();
    }

    /**
     * Constructor with essential fields.
     * 
     * @param imagenBase64 the image to identify
     * @param usuarioId the user requesting identification
     */
    public IAIdentificationResultDTO(String imagenBase64, Long usuarioId) {
        this();
        this.imagenBase64 = imagenBase64;
        this.usuarioId = usuarioId;
    }

    /**
     * Sets the confidence level based on percentage.
     * ALTA: >= 80%, MEDIA: 50-79%, BAJA: < 50%
     * 
     * @param porcentaje the confidence percentage
     */
    public void setConfianzaFromPercentage(BigDecimal porcentaje) {
        this.porcentajeConfianza = porcentaje;
        
        if (porcentaje == null) {
            this.nivelConfianza = "BAJA";
        } else if (porcentaje.compareTo(new BigDecimal("80")) >= 0) {
            this.nivelConfianza = "ALTA";
        } else if (porcentaje.compareTo(new BigDecimal("50")) >= 0) {
            this.nivelConfianza = "MEDIA";
        } else {
            this.nivelConfianza = "BAJA";
        }
    }

    /**
     * Checks if the identification was successful.
     * 
     * @return true if identification has results
     */
    public boolean isSuccessful() {
        return mensajeError == null && productoNombre != null && !productoNombre.trim().isEmpty();
    }

    /**
     * Checks if the identification has high confidence.
     * 
     * @return true if confidence is ALTA
     */
    public boolean isHighConfidence() {
        return "ALTA".equals(nivelConfianza);
    }

    /**
     * Checks if the identification requires manual review.
     * 
     * @return true if confidence is MEDIA or BAJA
     */
    public boolean requiresManualReview() {
        return "MEDIA".equals(nivelConfianza) || "BAJA".equals(nivelConfianza);
    }

    /**
     * Gets CSS class for confidence badge.
     * 
     * @return CSS class name
     */
    public String getConfianzaCssClass() {
        if (nivelConfianza == null) {
            return "badge-secondary";
        }
        
        return switch (nivelConfianza) {
            case "ALTA" -> "badge-success";
            case "MEDIA" -> "badge-warning";
            case "BAJA" -> "badge-danger";
            default -> "badge-secondary";
        };
    }

    /**
     * Gets icon class for confidence level.
     * 
     * @return icon class name
     */
    public String getConfianzaIconClass() {
        if (nivelConfianza == null) {
            return "fas fa-question-circle";
        }
        
        return switch (nivelConfianza) {
            case "ALTA" -> "fas fa-check-circle";
            case "MEDIA" -> "fas fa-exclamation-triangle";
            case "BAJA" -> "fas fa-times-circle";
            default -> "fas fa-question-circle";
        };
    }

    /**
     * Marks the result as accepted by user.
     * 
     * @param usuarioEmail the user who accepted the result
     */
    public void markAsAccepted(String usuarioEmail) {
        this.resultadoAceptado = true;
        this.fechaAceptacion = LocalDateTime.now();
        this.usuarioEmail = usuarioEmail;
    }

    /**
     * Creates a successful identification result.
     * 
     * @param imagenBase64 the analyzed image
     * @param productoNombre the identified product name
     * @param productoMarca the identified brand
     * @param productoTipo the identified type
     * @param confianza the confidence percentage
     * @param usuarioId the user who requested identification
     * @return successful identification result DTO
     */
    public static IAIdentificationResultDTO createSuccessful(String imagenBase64, String productoNombre, 
                                                           String productoMarca, String productoTipo, 
                                                           BigDecimal confianza, Long usuarioId) {
        IAIdentificationResultDTO result = new IAIdentificationResultDTO(imagenBase64, usuarioId);
        result.setProductoNombre(productoNombre);
        result.setProductoMarca(productoMarca);
        result.setProductoTipo(productoTipo);
        result.setConfianzaFromPercentage(confianza);
        return result;
    }

    /**
     * Creates a failed identification result.
     * 
     * @param imagenBase64 the analyzed image
     * @param mensajeError the error message
     * @param usuarioId the user who requested identification
     * @return failed identification result DTO
     */
    public static IAIdentificationResultDTO createFailed(String imagenBase64, String mensajeError, Long usuarioId) {
        IAIdentificationResultDTO result = new IAIdentificationResultDTO(imagenBase64, usuarioId);
        result.setMensajeError(mensajeError);
        result.setNivelConfianza("BAJA");
        result.setPorcentajeConfianza(BigDecimal.ZERO);
        return result;
    }

    // Getters and Setters

    public Long getIdentificacionId() {
        return identificacionId;
    }

    public void setIdentificacionId(Long identificacionId) {
        this.identificacionId = identificacionId;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }

    public String getNivelConfianza() {
        return nivelConfianza;
    }

    public void setNivelConfianza(String nivelConfianza) {
        this.nivelConfianza = nivelConfianza;
    }

    public BigDecimal getPorcentajeConfianza() {
        return porcentajeConfianza;
    }

    public void setPorcentajeConfianza(BigDecimal porcentajeConfianza) {
        this.porcentajeConfianza = porcentajeConfianza;
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

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCategoriaSugerida() {
        return categoriaSugerida;
    }

    public void setCategoriaSugerida(String categoriaSugerida) {
        this.categoriaSugerida = categoriaSugerida;
    }

    public String getAtributosDetectados() {
        return atributosDetectados;
    }

    public void setAtributosDetectados(String atributosDetectados) {
        this.atributosDetectados = atributosDetectados;
    }

    public List<String> getAlternativasSugeridas() {
        return alternativasSugeridas;
    }

    public void setAlternativasSugeridas(List<String> alternativasSugeridas) {
        this.alternativasSugeridas = alternativasSugeridas;
    }

    public Long getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }

    public void setTiempoProcesamiento(Long tiempoProcesamiento) {
        this.tiempoProcesamiento = tiempoProcesamiento;
    }

    public String getModeloVersion() {
        return modeloVersion;
    }

    public void setModeloVersion(String modeloVersion) {
        this.modeloVersion = modeloVersion;
    }

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
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

    public Long getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Long negocioId) {
        this.negocioId = negocioId;
    }

    public Boolean getResultadoAceptado() {
        return resultadoAceptado;
    }

    public void setResultadoAceptado(Boolean resultadoAceptado) {
        this.resultadoAceptado = resultadoAceptado;
    }

    public LocalDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(LocalDateTime fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public Boolean getConsumoQuota() {
        return consumoQuota;
    }

    public void setConsumoQuota(Boolean consumoQuota) {
        this.consumoQuota = consumoQuota;
    }

    @Override
    public String toString() {
        return "IAIdentificationResultDTO{" +
                "identificacionId=" + identificacionId +
                ", nivelConfianza='" + nivelConfianza + '\'' +
                ", porcentajeConfianza=" + porcentajeConfianza +
                ", productoNombre='" + productoNombre + '\'' +
                ", productoMarca='" + productoMarca + '\'' +
                ", productoTipo='" + productoTipo + '\'' +
                ", codigoBarras='" + codigoBarras + '\'' +
                ", categoriaSugerida='" + categoriaSugerida + '\'' +
                ", tiempoProcesamiento=" + tiempoProcesamiento +
                ", modeloVersion='" + modeloVersion + '\'' +
                ", fechaProcesamiento=" + fechaProcesamiento +
                ", usuarioId=" + usuarioId +
                ", usuarioEmail='" + usuarioEmail + '\'' +
                ", negocioId=" + negocioId +
                ", resultadoAceptado=" + resultadoAceptado +
                ", fechaAceptacion=" + fechaAceptacion +
                ", mensajeError='" + mensajeError + '\'' +
                ", consumoQuota=" + consumoQuota +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IAIdentificationResultDTO that = (IAIdentificationResultDTO) o;
        return identificacionId != null ? identificacionId.equals(that.identificacionId) : that.identificacionId == null;
    }

    @Override
    public int hashCode() {
        return identificacionId != null ? identificacionId.hashCode() : 0;
    }
}