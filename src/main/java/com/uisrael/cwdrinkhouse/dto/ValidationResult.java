package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Structured validation result for order creation validation.
 * Contains detailed validation feedback including errors, warnings,
 * and contextual information for debugging and user feedback.
 * 
 * Requirements: 3.1, 3.2, 3.4, 5.1, 5.2, 5.3, 5.4, 5.5
 */
public class ValidationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Overall validation status.
     */
    private boolean valid;

    /**
     * Timestamp when validation was performed.
     */
    private LocalDateTime timestamp;

    /**
     * List of validation errors that prevent processing.
     */
    private List<ValidationError> errors;

    /**
     * List of validation warnings that don't prevent processing.
     */
    private List<ValidationWarning> warnings;

    /**
     * Contextual information about the validation process.
     */
    private Map<String, Object> validationContext;

    /**
     * Field-specific validation results.
     */
    private Map<String, FieldValidationResult> fieldResults;

    /**
     * Business rule validation results.
     */
    private List<BusinessRuleValidation> businessRuleValidations;

    /**
     * Default constructor.
     */
    public ValidationResult() {
        this.timestamp = LocalDateTime.now();
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
        this.validationContext = new HashMap<>();
        this.fieldResults = new HashMap<>();
        this.businessRuleValidations = new ArrayList<>();
        this.valid = true; // Defaults to valid until errors are added
    }

    /**
     * Constructor with validation status.
     * 
     * @param valid the initial validation status
     */
    public ValidationResult(boolean valid) {
        this();
        this.valid = valid;
    }

    /**
     * Adds a validation error and marks the result as invalid.
     * 
     * @param error the validation error to add
     */
    public void addError(ValidationError error) {
        if (error != null) {
            this.errors.add(error);
            this.valid = false; // Any error makes the result invalid
        }
    }

    /**
     * Adds a validation error with field and message.
     * 
     * @param fieldName the field name
     * @param message the error message
     */
    public void addError(String fieldName, String message) {
        addError(new ValidationError(fieldName, message));
    }

    /**
     * Adds a validation error with field, message, and code.
     * 
     * @param fieldName the field name
     * @param message the error message
     * @param errorCode the error code
     */
    public void addError(String fieldName, String message, String errorCode) {
        addError(new ValidationError(fieldName, message, errorCode));
    }

    /**
     * Adds a validation warning.
     * 
     * @param warning the validation warning to add
     */
    public void addWarning(ValidationWarning warning) {
        if (warning != null) {
            this.warnings.add(warning);
        }
    }

    /**
     * Adds a validation warning with field and message.
     * 
     * @param fieldName the field name
     * @param message the warning message
     */
    public void addWarning(String fieldName, String message) {
        addWarning(new ValidationWarning(fieldName, message));
    }

    /**
     * Adds validation context information.
     * 
     * @param key the context key
     * @param value the context value
     */
    public void addValidationContext(String key, Object value) {
        if (key != null && value != null) {
            this.validationContext.put(key, value);
        }
    }

    /**
     * Sets field validation result.
     * 
     * @param fieldName the field name
     * @param result the field validation result
     */
    public void setFieldResult(String fieldName, FieldValidationResult result) {
        if (fieldName != null && result != null) {
            this.fieldResults.put(fieldName, result);
        }
    }

    /**
     * Adds a business rule validation result.
     * 
     * @param businessRuleValidation the business rule validation result
     */
    public void addBusinessRuleValidation(BusinessRuleValidation businessRuleValidation) {
        if (businessRuleValidation != null) {
            this.businessRuleValidations.add(businessRuleValidation);
            if (!businessRuleValidation.isPassed()) {
                this.valid = false;
            }
        }
    }

    /**
     * Checks if there are any validation errors.
     * 
     * @return true if there are validation errors
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Checks if there are any validation warnings.
     * 
     * @return true if there are validation warnings
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }

    /**
     * Gets the count of validation errors.
     * 
     * @return number of validation errors
     */
    public int getErrorCount() {
        return errors.size();
    }

    /**
     * Gets the count of validation warnings.
     * 
     * @return number of validation warnings
     */
    public int getWarningCount() {
        return warnings.size();
    }

    /**
     * Gets validation summary information.
     * 
     * @return formatted validation summary
     */
    public String getSummary() {
        return String.format("Validation Result: %s - %d errors, %d warnings", 
                valid ? "VALID" : "INVALID", getErrorCount(), getWarningCount());
    }

    /**
     * Validation error information.
     */
    public static class ValidationError implements Serializable {
        private String fieldName;
        private String message;
        private String errorCode;
        private Object rejectedValue;
        private LocalDateTime timestamp;

        public ValidationError() {
            this.timestamp = LocalDateTime.now();
        }

        public ValidationError(String fieldName, String message) {
            this();
            this.fieldName = fieldName;
            this.message = message;
        }

        public ValidationError(String fieldName, String message, String errorCode) {
            this(fieldName, message);
            this.errorCode = errorCode;
        }

        public ValidationError(String fieldName, String message, String errorCode, Object rejectedValue) {
            this(fieldName, message, errorCode);
            this.rejectedValue = rejectedValue;
        }

        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getErrorCode() { return errorCode; }
        public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
        
        public Object getRejectedValue() { return rejectedValue; }
        public void setRejectedValue(Object rejectedValue) { this.rejectedValue = rejectedValue; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "ValidationError{" +
                    "fieldName='" + fieldName + '\'' +
                    ", message='" + message + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    ", rejectedValue=" + rejectedValue +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    /**
     * Validation warning information.
     */
    public static class ValidationWarning implements Serializable {
        private String fieldName;
        private String message;
        private String warningCode;
        private Object value;
        private LocalDateTime timestamp;

        public ValidationWarning() {
            this.timestamp = LocalDateTime.now();
        }

        public ValidationWarning(String fieldName, String message) {
            this();
            this.fieldName = fieldName;
            this.message = message;
        }

        public ValidationWarning(String fieldName, String message, String warningCode) {
            this(fieldName, message);
            this.warningCode = warningCode;
        }

        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getWarningCode() { return warningCode; }
        public void setWarningCode(String warningCode) { this.warningCode = warningCode; }
        
        public Object getValue() { return value; }
        public void setValue(Object value) { this.value = value; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        @Override
        public String toString() {
            return "ValidationWarning{" +
                    "fieldName='" + fieldName + '\'' +
                    ", message='" + message + '\'' +
                    ", warningCode='" + warningCode + '\'' +
                    ", value=" + value +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    /**
     * Field validation result information.
     */
    public static class FieldValidationResult implements Serializable {
        private String fieldName;
        private boolean valid;
        private String validationMessage;
        private Object validatedValue;
        private List<String> appliedRules;

        public FieldValidationResult() {
            this.appliedRules = new ArrayList<>();
        }

        public FieldValidationResult(String fieldName, boolean valid, String validationMessage) {
            this();
            this.fieldName = fieldName;
            this.valid = valid;
            this.validationMessage = validationMessage;
        }

        public void addAppliedRule(String rule) {
            if (rule != null && !rule.trim().isEmpty()) {
                this.appliedRules.add(rule);
            }
        }

        // Getters and setters
        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getValidationMessage() { return validationMessage; }
        public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }
        
        public Object getValidatedValue() { return validatedValue; }
        public void setValidatedValue(Object validatedValue) { this.validatedValue = validatedValue; }
        
        public List<String> getAppliedRules() { return appliedRules; }
        public void setAppliedRules(List<String> appliedRules) { this.appliedRules = appliedRules != null ? appliedRules : new ArrayList<>(); }

        @Override
        public String toString() {
            return "FieldValidationResult{" +
                    "fieldName='" + fieldName + '\'' +
                    ", valid=" + valid +
                    ", validationMessage='" + validationMessage + '\'' +
                    ", validatedValue=" + validatedValue +
                    ", appliedRules=" + appliedRules.size() + " rules" +
                    '}';
        }
    }

    /**
     * Business rule validation information.
     */
    public static class BusinessRuleValidation implements Serializable {
        private String ruleName;
        private String ruleDescription;
        private boolean passed;
        private String failureMessage;
        private Map<String, Object> ruleContext;

        public BusinessRuleValidation() {
            this.ruleContext = new HashMap<>();
        }

        public BusinessRuleValidation(String ruleName, String ruleDescription, boolean passed) {
            this();
            this.ruleName = ruleName;
            this.ruleDescription = ruleDescription;
            this.passed = passed;
        }

        public BusinessRuleValidation(String ruleName, String ruleDescription, boolean passed, String failureMessage) {
            this(ruleName, ruleDescription, passed);
            this.failureMessage = failureMessage;
        }

        public void addRuleContext(String key, Object value) {
            if (key != null && value != null) {
                this.ruleContext.put(key, value);
            }
        }

        // Getters and setters
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        
        public String getRuleDescription() { return ruleDescription; }
        public void setRuleDescription(String ruleDescription) { this.ruleDescription = ruleDescription; }
        
        public boolean isPassed() { return passed; }
        public void setPassed(boolean passed) { this.passed = passed; }
        
        public String getFailureMessage() { return failureMessage; }
        public void setFailureMessage(String failureMessage) { this.failureMessage = failureMessage; }
        
        public Map<String, Object> getRuleContext() { return ruleContext; }
        public void setRuleContext(Map<String, Object> ruleContext) { this.ruleContext = ruleContext != null ? ruleContext : new HashMap<>(); }

        @Override
        public String toString() {
            return "BusinessRuleValidation{" +
                    "ruleName='" + ruleName + '\'' +
                    ", ruleDescription='" + ruleDescription + '\'' +
                    ", passed=" + passed +
                    ", failureMessage='" + failureMessage + '\'' +
                    ", ruleContext=" + ruleContext.size() + " entries" +
                    '}';
        }
    }

    // Main getters and setters

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors != null ? errors : new ArrayList<>();
        // Update valid status based on errors
        this.valid = this.errors.isEmpty();
    }

    public List<ValidationWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<ValidationWarning> warnings) {
        this.warnings = warnings != null ? warnings : new ArrayList<>();
    }

    public Map<String, Object> getValidationContext() {
        return validationContext;
    }

    public void setValidationContext(Map<String, Object> validationContext) {
        this.validationContext = validationContext != null ? validationContext : new HashMap<>();
    }

    public Map<String, FieldValidationResult> getFieldResults() {
        return fieldResults;
    }

    public void setFieldResults(Map<String, FieldValidationResult> fieldResults) {
        this.fieldResults = fieldResults != null ? fieldResults : new HashMap<>();
    }

    public List<BusinessRuleValidation> getBusinessRuleValidations() {
        return businessRuleValidations;
    }

    public void setBusinessRuleValidations(List<BusinessRuleValidation> businessRuleValidations) {
        this.businessRuleValidations = businessRuleValidations != null ? businessRuleValidations : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "valid=" + valid +
                ", timestamp=" + timestamp +
                ", errors=" + errors.size() + " errors" +
                ", warnings=" + warnings.size() + " warnings" +
                ", fieldResults=" + fieldResults.size() + " fields" +
                ", businessRuleValidations=" + businessRuleValidations.size() + " business rules" +
                '}';
    }
}