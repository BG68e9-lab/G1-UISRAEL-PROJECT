package com.uisrael.cwdrinkhouse.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for Order information.
 * Used in form binding, validation, and communication with backend API.
 * 
 * Requirements: 5.1-5.14, 14.1-14.10
 */
public class OrderDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * Order identifier (auto-generated).
     */
    private Long ordenCompraId;

    /**
     * Order reference code (auto-generated).
     * Format: ORD-YYYYMMDD-HHMMSS
     */
    private String codigoReferencia;

    /**
     * Provider identifier.
     * Required field linking to provider.
     */
    @NotNull(message = "El proveedor es obligatorio")
    private Long proveedorId;

    /**
     * Provider information (for display purposes).
     * Not validated as it's populated from backend.
     */
    private String proveedorRazonSocial;

    /**
     * Business identifier.
     * Populated automatically from the user session.
     */
    private Integer negocioId;

    /**
     * Order status.
     * Valid values: BORRADOR, ENVIADA, RECIBIDA, ANULADA
     */
    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(BORRADOR|ENVIADA|RECIBIDA|ANULADA)$", 
             message = "El estado debe ser BORRADOR, ENVIADA, RECIBIDA o ANULADA")
    private String estado;

    /**
     * Order total amount (calculated).
     * Sum of all order details (cantidad × precioUnitario).
     */
    @NotNull(message = "El total es obligatorio")
    @DecimalMin(value = "0.01", message = "El total debe ser mayor a cero")
    @Digits(integer = 12, fraction = 2, message = "El total debe tener máximo 2 decimales")
    private BigDecimal total;

    /**
     * Creation timestamp (read-only).
     */
    private LocalDateTime fechaCreacion;

    /**
     * User who created the order (read-only).
     */
    private String usuarioCreacion;

    /**
     * Order details (line items).
     * Must have at least one detail.
     */
    @NotEmpty(message = "La orden debe tener al menos un detalle")
    @Valid
    private List<OrderDetailDTO> detalles;

    /**
     * Order notes or comments.
     */
    @Size(max = 500, message = "Las observaciones no deben exceder los 500 caracteres")
    private String observaciones;

    /**
     * Optimistic locking version.
     * Used for concurrent access protection.
     * Updated on every modification to detect conflicts.
     */
    private Long version;

    /**
     * Default constructor.
     */
    public OrderDTO() {
        this.estado = "BORRADOR";
        this.total = BigDecimal.ZERO;
        this.detalles = new ArrayList<>();
    }

    /**
     * Constructor with essential fields.
     * 
     * @param proveedorId the provider ID
     */
    public OrderDTO(Long proveedorId) {
        this();
        this.proveedorId = proveedorId;
    }

    /**
     * Calculates the total amount from order details.
     * This method recalculates the total based on current details.
     * 
     * @return the calculated total amount
     */
    public BigDecimal calculateTotal() {
        if (detalles == null || detalles.isEmpty()) {
            this.total = BigDecimal.ZERO;
            return this.total;
        }

        BigDecimal calculatedTotal = detalles.stream()
                .filter(detalle -> detalle.getCantidad() != null && detalle.getPrecioUnitario() != null)
                .map(detalle -> BigDecimal.valueOf(detalle.getCantidad())
                        .multiply(detalle.getPrecioUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.total = calculatedTotal;
        return calculatedTotal;
    }

    /**
     * Validates if order can be transitioned to the given state.
     * 
     * @param newState the target state
     * @return true if transition is valid
     */
    public boolean canTransitionTo(String newState) {
        if (estado == null || newState == null) {
            return false;
        }

        return switch (estado) {
            case "BORRADOR" -> "ENVIADA".equals(newState) || "ANULADA".equals(newState);
            case "ENVIADA" -> "RECIBIDA".equals(newState) || "ANULADA".equals(newState);
            case "RECIBIDA", "ANULADA" -> false; // Final states
            default -> false;
        };
    }

    /**
     * Checks if the order can be edited (only BORRADOR state).
     * 
     * @return true if order can be edited
     */
    public boolean isEditable() {
        return "BORRADOR".equals(estado);
    }

    /**
     * Adds a detail to the order and recalculates total.
     * 
     * @param detalle the order detail to add
     */
    public void addDetalle(OrderDetailDTO detalle) {
        if (detalles == null) {
            detalles = new ArrayList<>();
        }
        detalles.add(detalle);
        calculateTotal();
    }

    /**
     * Removes a detail from the order and recalculates total.
     * 
     * @param index the index of the detail to remove
     */
    public void removeDetalle(int index) {
        if (detalles != null && index >= 0 && index < detalles.size()) {
            detalles.remove(index);
            calculateTotal();
        }
    }

    /**
     * Validates order data for creation and returns validation results.
     * This method performs comprehensive validation including:
     * - Basic field validation (local validation)
     * - Provider existence validation (requires OrderValidationService)
     * - Product existence validation (requires OrderValidationService)
     * - Business rules validation (requires OrderValidationService)
     * 
     * For full validation including external dependencies, use validateForCreationWithService().
     * 
     * @return list of validation errors found
     */
    public List<ValidationResult.ValidationError> validateForCreation() {
        List<ValidationResult.ValidationError> errors = new ArrayList<>();

        // Provider validation
        if (proveedorId == null) {
            errors.add(new ValidationResult.ValidationError("proveedorId", 
                "El proveedor es obligatorio", "REQUIRED_FIELD"));
        } else if (proveedorId <= 0) {
            errors.add(new ValidationResult.ValidationError("proveedorId", 
                "El ID del proveedor debe ser un valor positivo", "INVALID_VALUE"));
        }

        // Details validation
        if (detalles == null || detalles.isEmpty()) {
            errors.add(new ValidationResult.ValidationError("detalles", 
                "La orden debe tener al menos un detalle", "REQUIRED_FIELD"));
        } else {
            // Validate each detail
            for (int i = 0; i < detalles.size(); i++) {
                OrderDetailDTO detail = detalles.get(i);
                if (detail.getProductoId() == null) {
                    errors.add(new ValidationResult.ValidationError("detalles[" + i + "].productoId", 
                        "El producto es obligatorio", "REQUIRED_FIELD"));
                } else if (detail.getProductoId() <= 0) {
                    errors.add(new ValidationResult.ValidationError("detalles[" + i + "].productoId", 
                        "El ID del producto debe ser un valor positivo", "INVALID_VALUE"));
                }
                if (detail.getCantidad() == null || detail.getCantidad() <= 0) {
                    errors.add(new ValidationResult.ValidationError("detalles[" + i + "].cantidad", 
                        "La cantidad debe ser mayor a cero", "INVALID_VALUE"));
                }
                if (detail.getPrecioUnitario() == null || detail.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add(new ValidationResult.ValidationError("detalles[" + i + "].precioUnitario", 
                        "El precio unitario debe ser mayor a cero", "INVALID_VALUE"));
                }
            }
        }

        // Total validation
        BigDecimal calculatedTotal = calculateTotal();
        if (total == null || total.compareTo(calculatedTotal) != 0) {
            errors.add(new ValidationResult.ValidationError("total", 
                "El total calculado no coincide con el total especificado", "CALCULATION_MISMATCH"));
        }

        // State validation
        if (estado != null && !isValidOrderState(estado)) {
            errors.add(new ValidationResult.ValidationError("estado", 
                "Estado inválido: " + estado + ". Debe ser uno de: BORRADOR, ENVIADA, RECIBIDA, ANULADA", "INVALID_STATE"));
        }

        return errors;
    }

    /**
     * Performs comprehensive validation using OrderValidationService.
     * This method provides full validation including external dependencies:
     * - Provider existence validation against database
     * - Product existence validation against database  
     * - Business rules validation
     * - Total calculation validation
     * - Comprehensive error reporting
     * 
     * Note: This method requires an OrderValidationService instance and should be called
     * from service layer components that have access to validation services.
     * 
     * @param validationService the order validation service for comprehensive validation
     * @return complete validation result with detailed error information
     */
    public ValidationResult validateForCreationWithService(com.uisrael.cwdrinkhouse.service.OrderValidationService validationService) {
        if (validationService == null) {
            throw new IllegalArgumentException("OrderValidationService is required for comprehensive validation");
        }
        
        return validationService.validateOrderData(this);
    }

    /**
     * Checks if the order has valid provider reference.
     * This method performs basic validation only (non-null and positive value).
     * For database validation, use hasValidProviderReferenceWithService().
     * 
     * @return true if provider reference appears valid (non-null and positive)
     */
    public boolean hasValidProviderReference() {
        return proveedorId != null && proveedorId > 0;
    }

    /**
     * Checks if the order has valid provider reference with database validation.
     * This method validates against the actual provider database records.
     * 
     * @param validationService the order validation service for database checks
     * @return true if provider exists and is active in the database
     */
    public boolean hasValidProviderReferenceWithService(com.uisrael.cwdrinkhouse.service.OrderValidationService validationService) {
        if (validationService == null) {
            throw new IllegalArgumentException("OrderValidationService is required for database validation");
        }
        
        ValidationResult result = validationService.validateProviderExists(this.proveedorId);
        return result.isValid();
    }

    /**
     * Checks if the order has valid product references in details.
     * This method performs basic validation only (non-null and positive values).
     * For database validation, use hasValidProductReferencesWithService().
     * 
     * @return true if all product references appear valid
     */
    public boolean hasValidProductReferences() {
        if (detalles == null || detalles.isEmpty()) {
            return false;
        }

        return detalles.stream()
                .allMatch(detail -> detail.getProductoId() != null && detail.getProductoId() > 0);
    }

    /**
     * Checks if the order has valid product references with database validation.
     * This method validates against the actual product database records.
     * 
     * @param validationService the order validation service for database checks
     * @return true if all products exist and are available in the database
     */
    public boolean hasValidProductReferencesWithService(com.uisrael.cwdrinkhouse.service.OrderValidationService validationService) {
        if (validationService == null) {
            throw new IllegalArgumentException("OrderValidationService is required for database validation");
        }
        
        if (detalles == null || detalles.isEmpty()) {
            return false;
        }
        
        List<Long> productIds = detalles.stream()
                .map(OrderDetailDTO::getProductoId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toList());
        
        if (productIds.isEmpty()) {
            return false;
        }
        
        ValidationResult result = validationService.validateProductsExist(productIds);
        return result.isValid();
    }

    /**
     * Checks if the order has proper total calculation.
     * 
     * @return true if the stored total matches the calculated total
     */
    public boolean hasProfessionalTotalCalculation() {
        if (total == null) {
            return false;
        }

        BigDecimal calculatedTotal = calculateTotal();
        return total.compareTo(calculatedTotal) == 0;
    }

    /**
     * Generates a comprehensive diagnostic summary for troubleshooting.
     * Includes validation status, calculation details, and error information.
     * 
     * @return formatted diagnostic summary with detailed information
     */
    public String generateDiagnosticSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=== OrderDTO Diagnostic Summary ===\n");
        summary.append("Timestamp: ").append(java.time.LocalDateTime.now()).append("\n");
        summary.append("- Order ID: ").append(ordenCompraId != null ? ordenCompraId : "NOT_SET").append("\n");
        summary.append("- Reference Code: ").append(codigoReferencia != null ? codigoReferencia : "NOT_SET").append("\n");
        summary.append("- Provider ID: ").append(proveedorId != null ? proveedorId : "NULL").append("\n");
        summary.append("- Provider Name: ").append(proveedorRazonSocial != null ? proveedorRazonSocial : "NOT_SET").append("\n");
        summary.append("- Business ID: ").append(negocioId != null ? negocioId : "NULL").append("\n");
        summary.append("- Status: ").append(estado != null ? estado : "NULL").append("\n");
        summary.append("- Creation Date: ").append(fechaCreacion != null ? fechaCreacion : "NOT_SET").append("\n");
        summary.append("- Created By: ").append(usuarioCreacion != null ? usuarioCreacion : "NOT_SET").append("\n");
        
        // Financial information
        summary.append("\n--- Financial Information ---\n");
        summary.append("- Stored Total: ").append(total != null ? total : "NULL").append("\n");
        BigDecimal calculatedTotal = calculateTotal();
        summary.append("- Calculated Total: ").append(calculatedTotal).append("\n");
        summary.append("- Total Match: ").append(hasProfessionalTotalCalculation()).append("\n");
        if (total != null && calculatedTotal != null) {
            BigDecimal difference = total.subtract(calculatedTotal).abs();
            summary.append("- Difference: ").append(difference).append("\n");
        }
        
        // Details analysis
        summary.append("\n--- Order Details Analysis ---\n");
        summary.append("- Details Count: ").append(detalles != null ? detalles.size() : 0).append("\n");
        summary.append("- Has Details: ").append(detalles != null && !detalles.isEmpty()).append("\n");
        
        if (detalles != null && !detalles.isEmpty()) {
            for (int i = 0; i < detalles.size(); i++) {
                OrderDetailDTO detail = detalles.get(i);
                summary.append("  Detail ").append(i + 1).append(":\n");
                summary.append("    - Product ID: ").append(detail.getProductoId() != null ? detail.getProductoId() : "NULL").append("\n");
                summary.append("    - Product Name: ").append(detail.getProductoNombre() != null ? detail.getProductoNombre() : "NOT_SET").append("\n");
                summary.append("    - Quantity: ").append(detail.getCantidad() != null ? detail.getCantidad() : "NULL").append("\n");
                summary.append("    - Unit Price: ").append(detail.getPrecioUnitario() != null ? detail.getPrecioUnitario() : "NULL").append("\n");
                summary.append("    - Subtotal: ").append(detail.getSubtotal() != null ? detail.getSubtotal() : "NULL").append("\n");
                if (detail.getCantidad() != null && detail.getPrecioUnitario() != null) {
                    BigDecimal expectedSubtotal = BigDecimal.valueOf(detail.getCantidad()).multiply(detail.getPrecioUnitario());
                    summary.append("    - Expected Subtotal: ").append(expectedSubtotal).append("\n");
                }
            }
        }
        
        // Validation summary
        summary.append("\n--- Validation Summary ---\n");
        summary.append("- Valid Provider Ref: ").append(hasValidProviderReference()).append("\n");
        summary.append("- Valid Product Refs: ").append(hasValidProductReferences()).append("\n");
        summary.append("- Total Calculation OK: ").append(hasProfessionalTotalCalculation()).append("\n");
        summary.append("- Is Editable: ").append(isEditable()).append("\n");
        
        List<ValidationResult.ValidationError> errors = validateForCreation();
        summary.append("- Local Validation Errors: ").append(errors.size()).append("\n");
        
        if (!errors.isEmpty()) {
            summary.append("\n--- Validation Error Details ---\n");
            errors.forEach(error -> 
                summary.append("  * ").append(error.getFieldName())
                       .append(": ").append(error.getMessage())
                       .append(" (Code: ").append(error.getErrorCode()).append(")\n"));
        }
        
        // Business rules summary
        summary.append("\n--- Business Rules Check ---\n");
        if (estado != null) {
            summary.append("- Valid State: ").append(isValidOrderState(estado)).append("\n");
            summary.append("- Can Edit: ").append(canTransitionTo("ENVIADA")).append("\n");
            summary.append("- Can Send: ").append(canTransitionTo("ENVIADA")).append("\n");
            summary.append("- Can Cancel: ").append(canTransitionTo("ANULADA")).append("\n");
        }
        
        // Additional diagnostics
        summary.append("\n--- Additional Diagnostics ---\n");
        summary.append("- Hash Code: ").append(this.hashCode()).append("\n");
        summary.append("- Object Class: ").append(this.getClass().getSimpleName()).append("\n");
        summary.append("- Observations Length: ").append(observaciones != null ? observaciones.length() : 0).append(" chars\n");
        
        summary.append("=== End Diagnostic Summary ===\n");
        
        return summary.toString();
    }

    /**
     * Extracts comprehensive validation context for detailed error reporting.
     * Provides structured information for diagnostic and debugging purposes.
     * 
     * @return map containing detailed validation context information
     */
    public java.util.Map<String, Object> extractValidationContext() {
        java.util.Map<String, Object> context = new java.util.HashMap<>();
        
        // Basic order information
        context.put("extractionTimestamp", java.time.LocalDateTime.now());
        context.put("ordenCompraId", ordenCompraId);
        context.put("codigoReferencia", codigoReferencia);
        context.put("proveedorId", proveedorId);
        context.put("proveedorRazonSocial", proveedorRazonSocial);
        context.put("negocioId", negocioId);
        context.put("estado", estado);
        context.put("fechaCreacion", fechaCreacion);
        context.put("usuarioCreacion", usuarioCreacion);
        context.put("observaciones", observaciones);
        
        // Financial context
        java.util.Map<String, Object> financialContext = new java.util.HashMap<>();
        financialContext.put("storedTotal", total);
        BigDecimal calculatedTotal = calculateTotal();
        financialContext.put("calculatedTotal", calculatedTotal);
        financialContext.put("totalMatch", hasProfessionalTotalCalculation());
        if (total != null && calculatedTotal != null) {
            BigDecimal difference = total.subtract(calculatedTotal).abs();
            financialContext.put("difference", difference);
            financialContext.put("percentageDifference", 
                total.compareTo(BigDecimal.ZERO) != 0 ? 
                    difference.divide(total, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : 
                    BigDecimal.ZERO);
        }
        context.put("financial", financialContext);
        
        // Details analysis
        java.util.Map<String, Object> detailsContext = new java.util.HashMap<>();
        detailsContext.put("count", detalles != null ? detalles.size() : 0);
        detailsContext.put("hasDetails", detalles != null && !detalles.isEmpty());
        
        if (detalles != null && !detalles.isEmpty()) {
            List<java.util.Map<String, Object>> detallesAnalysis = new ArrayList<>();
            BigDecimal totalFromDetails = BigDecimal.ZERO;
            int validDetailsCount = 0;
            
            for (int i = 0; i < detalles.size(); i++) {
                OrderDetailDTO detail = detalles.get(i);
                java.util.Map<String, Object> detailContext = new java.util.HashMap<>();
                
                // Basic detail information
                detailContext.put("index", i);
                detailContext.put("detalleId", detail.getDetalleId());
                detailContext.put("productoId", detail.getProductoId());
                detailContext.put("productoNombre", detail.getProductoNombre());
                detailContext.put("productoMarca", detail.getProductoMarca());
                detailContext.put("productoTipo", detail.getProductoTipo());
                detailContext.put("cantidad", detail.getCantidad());
                detailContext.put("precioUnitario", detail.getPrecioUnitario());
                detailContext.put("subtotal", detail.getSubtotal());
                detailContext.put("observaciones", detail.getObservaciones());
                
                // Validation context for this detail
                java.util.Map<String, Boolean> detailValidation = new java.util.HashMap<>();
                detailValidation.put("hasValidProductId", detail.getProductoId() != null && detail.getProductoId() > 0);
                detailValidation.put("hasValidQuantity", detail.getCantidad() != null && detail.getCantidad() > 0);
                detailValidation.put("hasValidPrice", detail.getPrecioUnitario() != null && detail.getPrecioUnitario().compareTo(BigDecimal.ZERO) > 0);
                
                // Calculate expected subtotal
                BigDecimal expectedSubtotal = null;
                if (detail.getCantidad() != null && detail.getPrecioUnitario() != null) {
                    expectedSubtotal = BigDecimal.valueOf(detail.getCantidad()).multiply(detail.getPrecioUnitario());
                    detailContext.put("expectedSubtotal", expectedSubtotal);
                    detailValidation.put("subtotalMatches", 
                        detail.getSubtotal() != null && detail.getSubtotal().compareTo(expectedSubtotal) == 0);
                    
                    if (detailValidation.get("hasValidProductId") && detailValidation.get("hasValidQuantity") && detailValidation.get("hasValidPrice")) {
                        totalFromDetails = totalFromDetails.add(expectedSubtotal);
                        validDetailsCount++;
                    }
                } else {
                    detailValidation.put("subtotalMatches", false);
                }
                
                detailContext.put("validation", detailValidation);
                detallesAnalysis.add(detailContext);
            }
            
            detailsContext.put("items", detallesAnalysis);
            detailsContext.put("validDetailsCount", validDetailsCount);
            detailsContext.put("totalFromDetails", totalFromDetails);
        }
        context.put("details", detailsContext);
        
        // Validation summary
        java.util.Map<String, Object> validationSummary = new java.util.HashMap<>();
        validationSummary.put("hasValidProviderRef", hasValidProviderReference());
        validationSummary.put("hasValidProductRefs", hasValidProductReferences());
        validationSummary.put("totalCalculationOK", hasProfessionalTotalCalculation());
        validationSummary.put("isEditable", isEditable());
        
        List<ValidationResult.ValidationError> localErrors = validateForCreation();
        validationSummary.put("localValidationErrorCount", localErrors.size());
        validationSummary.put("isLocallyValid", localErrors.isEmpty());
        
        if (!localErrors.isEmpty()) {
            List<java.util.Map<String, Object>> errorDetails = new ArrayList<>();
            localErrors.forEach(error -> {
                java.util.Map<String, Object> errorContext = new java.util.HashMap<>();
                errorContext.put("fieldName", error.getFieldName());
                errorContext.put("message", error.getMessage());
                errorContext.put("errorCode", error.getErrorCode());
                errorContext.put("timestamp", error.getTimestamp());
                errorDetails.add(errorContext);
            });
            validationSummary.put("errors", errorDetails);
        }
        
        context.put("validation", validationSummary);
        
        // State and business rules context
        java.util.Map<String, Object> businessContext = new java.util.HashMap<>();
        if (estado != null) {
            businessContext.put("currentState", estado);
            businessContext.put("isValidState", isValidOrderState(estado));
            businessContext.put("canEdit", "BORRADOR".equals(estado));
            businessContext.put("canSend", canTransitionTo("ENVIADA"));
            businessContext.put("canReceive", canTransitionTo("RECIBIDA"));
            businessContext.put("canCancel", canTransitionTo("ANULADA"));
            
            // Available transitions
            List<String> availableTransitions = new ArrayList<>();
            if (canTransitionTo("ENVIADA")) availableTransitions.add("ENVIADA");
            if (canTransitionTo("RECIBIDA")) availableTransitions.add("RECIBIDA");
            if (canTransitionTo("ANULADA")) availableTransitions.add("ANULADA");
            businessContext.put("availableTransitions", availableTransitions);
        }
        context.put("business", businessContext);
        
        // Technical context
        java.util.Map<String, Object> technicalContext = new java.util.HashMap<>();
        technicalContext.put("objectHashCode", this.hashCode());
        technicalContext.put("className", this.getClass().getName());
        technicalContext.put("serialVersionUID", serialVersionUID);
        technicalContext.put("observationsLength", observaciones != null ? observaciones.length() : 0);
        context.put("technical", technicalContext);
        
        return context;
    }

    // Private helper methods

    /**
     * Validates if the given state is a valid order state.
     * 
     * @param estado the state to validate
     * @return true if the state is valid
     */
    private boolean isValidOrderState(String estado) {
        if (estado == null) {
            return false;
        }
        String upperState = estado.trim().toUpperCase();
        return "BORRADOR".equals(upperState) || "ENVIADA".equals(upperState) || 
               "RECIBIDA".equals(upperState) || "ANULADA".equals(upperState);
    }

    // Getters and Setters

    public Long getOrdenCompraId() {
        return ordenCompraId;
    }

    public void setOrdenCompraId(Long ordenCompraId) {
        this.ordenCompraId = ordenCompraId;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public String getProveedorRazonSocial() {
        return proveedorRazonSocial;
    }

    public void setProveedorRazonSocial(String proveedorRazonSocial) {
        this.proveedorRazonSocial = proveedorRazonSocial;
    }

    public Integer getNegocioId() {
        return negocioId;
    }

    public void setNegocioId(Integer negocioId) {
        this.negocioId = negocioId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
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

    public List<OrderDetailDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<OrderDetailDTO> detalles) {
        this.detalles = detalles;
        calculateTotal(); // Recalculate when details are set
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "ordenCompraId=" + ordenCompraId +
                ", codigoReferencia='" + codigoReferencia + '\'' +
                ", proveedorId=" + proveedorId +
                ", proveedorRazonSocial='" + proveedorRazonSocial + '\'' +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", fechaCreacion=" + fechaCreacion +
                ", usuarioCreacion='" + usuarioCreacion + '\'' +
                ", detalles=" + (detalles != null ? detalles.size() + " items" : "null") +
                ", observaciones='" + observaciones + '\'' +
                ", version=" + version +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDTO orderDTO = (OrderDTO) o;
        return ordenCompraId != null ? ordenCompraId.equals(orderDTO.ordenCompraId) : orderDTO.ordenCompraId == null;
    }

    @Override
    public int hashCode() {
        return ordenCompraId != null ? ordenCompraId.hashCode() : 0;
    }
}