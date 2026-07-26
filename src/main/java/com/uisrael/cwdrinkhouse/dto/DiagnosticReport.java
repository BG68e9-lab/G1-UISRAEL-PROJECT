package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive diagnostic report for order creation failures.
 * Contains detailed analysis of failure root causes and recommended actions.
 * 
 * Requirements: 1.1, 1.2, 1.6
 */
public class DiagnosticReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique correlation ID for tracking this diagnostic session.
     */
    private String correlationId;

    /**
     * Timestamp when the diagnostic was performed.
     */
    private LocalDateTime timestamp;

    /**
     * Category of the diagnostic issue.
     */
    private DiagnosticCategory category;

    /**
     * Primary root cause of the failure.
     */
    private String rootCause;

    /**
     * Additional contributing factors to the failure.
     */
    private List<String> contributingFactors;

    /**
     * Technical diagnostic data and metrics.
     */
    private Map<String, Object> diagnosticData;

    /**
     * List of recommended actions to resolve the issue.
     */
    private List<String> recommendedActions;

    /**
     * Severity level of the diagnostic issue.
     */
    private DiagnosticSeverity severity;

    /**
     * Component layer where the failure occurred.
     */
    private String failurePoint;

    /**
     * Complete stack trace information.
     */
    private String stackTrace;

    /**
     * Default constructor.
     */
    public DiagnosticReport() {
        this.timestamp = LocalDateTime.now();
        this.contributingFactors = new ArrayList<>();
        this.diagnosticData = new HashMap<>();
        this.recommendedActions = new ArrayList<>();
        this.severity = DiagnosticSeverity.UNKNOWN;
    }

    /**
     * Constructor with essential fields.
     * 
     * @param correlationId unique correlation ID
     * @param category diagnostic category
     * @param rootCause primary root cause
     */
    public DiagnosticReport(String correlationId, DiagnosticCategory category, String rootCause) {
        this();
        this.correlationId = correlationId;
        this.category = category;
        this.rootCause = rootCause;
    }

    /**
     * Adds a contributing factor to the failure analysis.
     * 
     * @param factor the contributing factor to add
     */
    public void addContributingFactor(String factor) {
        if (factor != null && !factor.trim().isEmpty()) {
            this.contributingFactors.add(factor);
        }
    }

    /**
     * Adds diagnostic data entry.
     * 
     * @param key the data key
     * @param value the data value
     */
    public void addDiagnosticData(String key, Object value) {
        if (key != null && !key.trim().isEmpty()) {
            this.diagnosticData.put(key, value);
        }
    }

    /**
     * Adds a recommended action to resolve the issue.
     * 
     * @param action the recommended action
     */
    public void addRecommendedAction(String action) {
        if (action != null && !action.trim().isEmpty()) {
            this.recommendedActions.add(action);
        }
    }

    /**
     * Gets summary information about the diagnostic report.
     * 
     * @return formatted summary string
     */
    public String getSummary() {
        return String.format("Diagnostic Report [%s]: %s - %s (Severity: %s)",
                correlationId, category, rootCause, severity);
    }

    /**
     * Diagnostic category enumeration.
     */
    public enum DiagnosticCategory {
        VALIDATION_ERROR,
        CONNECTIVITY_ERROR,
        DATABASE_ERROR,
        BUSINESS_RULE_VIOLATION,
        SYSTEM_ERROR,
        TIMEOUT_ERROR,
        CONFIGURATION_ERROR,
        UNKNOWN
    }

    /**
     * Diagnostic severity enumeration.
     */
    public enum DiagnosticSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL,
        UNKNOWN
    }

    // Getters and Setters

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DiagnosticCategory getCategory() {
        return category;
    }

    public void setCategory(DiagnosticCategory category) {
        this.category = category;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public List<String> getContributingFactors() {
        return contributingFactors;
    }

    public void setContributingFactors(List<String> contributingFactors) {
        this.contributingFactors = contributingFactors != null ? contributingFactors : new ArrayList<>();
    }

    public Map<String, Object> getDiagnosticData() {
        return diagnosticData;
    }

    public void setDiagnosticData(Map<String, Object> diagnosticData) {
        this.diagnosticData = diagnosticData != null ? diagnosticData : new HashMap<>();
    }

    public List<String> getRecommendedActions() {
        return recommendedActions;
    }

    public void setRecommendedActions(List<String> recommendedActions) {
        this.recommendedActions = recommendedActions != null ? recommendedActions : new ArrayList<>();
    }

    public DiagnosticSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(DiagnosticSeverity severity) {
        this.severity = severity;
    }

    public String getFailurePoint() {
        return failurePoint;
    }

    public void setFailurePoint(String failurePoint) {
        this.failurePoint = failurePoint;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public String toString() {
        return "DiagnosticReport{" +
                "correlationId='" + correlationId + '\'' +
                ", timestamp=" + timestamp +
                ", category=" + category +
                ", rootCause='" + rootCause + '\'' +
                ", severity=" + severity +
                ", failurePoint='" + failurePoint + '\'' +
                ", contributingFactors=" + contributingFactors.size() + " factors" +
                ", recommendedActions=" + recommendedActions.size() + " actions" +
                '}';
    }
}