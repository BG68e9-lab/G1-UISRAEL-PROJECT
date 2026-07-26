package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User-friendly error response for order creation failures.
 * Provides clear, actionable error messages for end users while
 * maintaining technical details for system administrators.
 * 
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6
 */
public class UserFriendlyError implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User-facing error message in clear, non-technical language.
     */
    private String userMessage;

    /**
     * Technical error message for system administrators and developers.
     */
    private String technicalMessage;

    /**
     * Unique correlation ID for error tracking and support.
     */
    private String correlationId;

    /**
     * Timestamp when the error occurred.
     */
    private LocalDateTime timestamp;

    /**
     * Error category for classification and handling.
     */
    private ErrorCategory category;

    /**
     * Error severity level.
     */
    private ErrorSeverity severity;

    /**
     * HTTP status code associated with this error.
     */
    private int httpStatus;

    /**
     * List of suggested actions for the user.
     */
    private List<String> suggestedActions;

    /**
     * Field-specific error messages for form validation.
     */
    private Map<String, String> fieldErrors;

    /**
     * Additional contextual information for troubleshooting.
     */
    private Map<String, Object> errorContext;

    /**
     * Support information for contacting help desk.
     */
    private SupportInformation supportInformation;

    /**
     * Retry information if the error is transient.
     */
    private RetryInformation retryInformation;

    /**
     * Default constructor.
     */
    public UserFriendlyError() {
        this.timestamp = LocalDateTime.now();
        this.suggestedActions = new ArrayList<>();
        this.fieldErrors = new HashMap<>();
        this.errorContext = new HashMap<>();
        this.category = ErrorCategory.UNKNOWN;
        this.severity = ErrorSeverity.MEDIUM;
    }

    /**
     * Constructor with essential error information.
     * 
     * @param userMessage user-facing error message
     * @param technicalMessage technical error message
     * @param correlationId unique correlation ID
     */
    public UserFriendlyError(String userMessage, String technicalMessage, String correlationId) {
        this();
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
        this.correlationId = correlationId;
    }

    /**
     * Constructor with error category and HTTP status.
     * 
     * @param userMessage user-facing error message
     * @param technicalMessage technical error message
     * @param correlationId unique correlation ID
     * @param category error category
     * @param httpStatus HTTP status code
     */
    public UserFriendlyError(String userMessage, String technicalMessage, String correlationId,
                            ErrorCategory category, int httpStatus) {
        this(userMessage, technicalMessage, correlationId);
        this.category = category;
        this.httpStatus = httpStatus;
    }

    /**
     * Adds a suggested action for the user.
     * 
     * @param action the suggested action
     */
    public void addSuggestedAction(String action) {
        if (action != null && !action.trim().isEmpty()) {
            this.suggestedActions.add(action);
        }
    }

    /**
     * Adds a field-specific error message.
     * 
     * @param fieldName the field name
     * @param errorMessage the error message for this field
     */
    public void addFieldError(String fieldName, String errorMessage) {
        if (fieldName != null && errorMessage != null) {
            this.fieldErrors.put(fieldName, errorMessage);
        }
    }

    /**
     * Adds error context information.
     * 
     * @param key the context key
     * @param value the context value
     */
    public void addErrorContext(String key, Object value) {
        if (key != null && value != null) {
            this.errorContext.put(key, value);
        }
    }

    /**
     * Checks if this error has field-specific validation errors.
     * 
     * @return true if there are field errors
     */
    public boolean hasFieldErrors() {
        return !fieldErrors.isEmpty();
    }

    /**
     * Checks if this error has suggested actions.
     * 
     * @return true if there are suggested actions
     */
    public boolean hasSuggestedActions() {
        return !suggestedActions.isEmpty();
    }

    /**
     * Checks if this error is retryable.
     * 
     * @return true if the error is retryable
     */
    public boolean isRetryable() {
        return retryInformation != null && retryInformation.isRetryable();
    }

    /**
     * Gets a formatted error summary for logging.
     * 
     * @return formatted error summary
     */
    public String getErrorSummary() {
        return String.format("Error [%s]: %s (Category: %s, Severity: %s, HTTP: %d)",
                correlationId, userMessage, category, severity, httpStatus);
    }

    /**
     * Error category enumeration.
     */
    public enum ErrorCategory {
        VALIDATION_ERROR,
        AUTHENTICATION_ERROR,
        AUTHORIZATION_ERROR,
        BUSINESS_RULE_VIOLATION,
        RESOURCE_NOT_FOUND,
        SERVICE_UNAVAILABLE,
        TIMEOUT_ERROR,
        NETWORK_ERROR,
        DATABASE_ERROR,
        CONFIGURATION_ERROR,
        SYSTEM_ERROR,
        UNKNOWN
    }

    /**
     * Error severity enumeration.
     */
    public enum ErrorSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Support information for error resolution.
     */
    public static class SupportInformation implements Serializable {
        private String supportEmail;
        private String supportPhone;
        private String ticketUrl;
        private String documentationUrl;
        private String troubleshootingGuide;

        public SupportInformation() {}

        public SupportInformation(String supportEmail, String supportPhone, String ticketUrl) {
            this.supportEmail = supportEmail;
            this.supportPhone = supportPhone;
            this.ticketUrl = ticketUrl;
        }

        // Getters and setters
        public String getSupportEmail() { return supportEmail; }
        public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }
        
        public String getSupportPhone() { return supportPhone; }
        public void setSupportPhone(String supportPhone) { this.supportPhone = supportPhone; }
        
        public String getTicketUrl() { return ticketUrl; }
        public void setTicketUrl(String ticketUrl) { this.ticketUrl = ticketUrl; }
        
        public String getDocumentationUrl() { return documentationUrl; }
        public void setDocumentationUrl(String documentationUrl) { this.documentationUrl = documentationUrl; }
        
        public String getTroubleshootingGuide() { return troubleshootingGuide; }
        public void setTroubleshootingGuide(String troubleshootingGuide) { this.troubleshootingGuide = troubleshootingGuide; }

        @Override
        public String toString() {
            return "SupportInformation{" +
                    "supportEmail='" + supportEmail + '\'' +
                    ", supportPhone='" + supportPhone + '\'' +
                    ", ticketUrl='" + ticketUrl + '\'' +
                    ", documentationUrl='" + documentationUrl + '\'' +
                    ", troubleshootingGuide='" + troubleshootingGuide + '\'' +
                    '}';
        }
    }

    /**
     * Retry information for transient errors.
     */
    public static class RetryInformation implements Serializable {
        private boolean retryable;
        private int maxRetryAttempts;
        private long retryDelayMillis;
        private String retryStrategy;
        private LocalDateTime nextRetryTime;

        public RetryInformation() {}

        public RetryInformation(boolean retryable, int maxRetryAttempts, long retryDelayMillis) {
            this.retryable = retryable;
            this.maxRetryAttempts = maxRetryAttempts;
            this.retryDelayMillis = retryDelayMillis;
        }

        // Getters and setters
        public boolean isRetryable() { return retryable; }
        public void setRetryable(boolean retryable) { this.retryable = retryable; }
        
        public int getMaxRetryAttempts() { return maxRetryAttempts; }
        public void setMaxRetryAttempts(int maxRetryAttempts) { this.maxRetryAttempts = maxRetryAttempts; }
        
        public long getRetryDelayMillis() { return retryDelayMillis; }
        public void setRetryDelayMillis(long retryDelayMillis) { this.retryDelayMillis = retryDelayMillis; }
        
        public String getRetryStrategy() { return retryStrategy; }
        public void setRetryStrategy(String retryStrategy) { this.retryStrategy = retryStrategy; }
        
        public LocalDateTime getNextRetryTime() { return nextRetryTime; }
        public void setNextRetryTime(LocalDateTime nextRetryTime) { this.nextRetryTime = nextRetryTime; }

        @Override
        public String toString() {
            return "RetryInformation{" +
                    "retryable=" + retryable +
                    ", maxRetryAttempts=" + maxRetryAttempts +
                    ", retryDelayMillis=" + retryDelayMillis +
                    ", retryStrategy='" + retryStrategy + '\'' +
                    ", nextRetryTime=" + nextRetryTime +
                    '}';
        }
    }

    // Main getters and setters

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = technicalMessage;
    }

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

    public ErrorCategory getCategory() {
        return category;
    }

    public void setCategory(ErrorCategory category) {
        this.category = category;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(ErrorSeverity severity) {
        this.severity = severity;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public List<String> getSuggestedActions() {
        return suggestedActions;
    }

    public void setSuggestedActions(List<String> suggestedActions) {
        this.suggestedActions = suggestedActions != null ? suggestedActions : new ArrayList<>();
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors != null ? fieldErrors : new HashMap<>();
    }

    public Map<String, Object> getErrorContext() {
        return errorContext;
    }

    public void setErrorContext(Map<String, Object> errorContext) {
        this.errorContext = errorContext != null ? errorContext : new HashMap<>();
    }

    public SupportInformation getSupportInformation() {
        return supportInformation;
    }

    public void setSupportInformation(SupportInformation supportInformation) {
        this.supportInformation = supportInformation;
    }

    public RetryInformation getRetryInformation() {
        return retryInformation;
    }

    public void setRetryInformation(RetryInformation retryInformation) {
        this.retryInformation = retryInformation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserFriendlyError that = (UserFriendlyError) obj;
        return correlationId != null ? correlationId.equals(that.correlationId) : that.correlationId == null;
    }

    @Override
    public int hashCode() {
        return correlationId != null ? correlationId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UserFriendlyError{" +
                "userMessage='" + userMessage + '\'' +
                ", technicalMessage='" + technicalMessage + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", timestamp=" + timestamp +
                ", category=" + category +
                ", severity=" + severity +
                ", httpStatus=" + httpStatus +
                ", suggestedActions=" + suggestedActions.size() + " actions" +
                ", fieldErrors=" + fieldErrors.size() + " field errors" +
                ", retryable=" + isRetryable() +
                '}';
    }
}