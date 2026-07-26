package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a validation error with field, message, and additional metadata.
 * Serializable for logging and transmission.
 * 
 * Requirements: 5.1, 5.2
 */
public class ValidationError implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String field;
    private String message;
    private String code;
    private Object rejectedValue;
    private Map<String, Object> details;
    
    /**
     * Default constructor.
     */
    public ValidationError() {
        this.details = new HashMap<>();
    }
    
    /**
     * Constructor with field and message.
     */
    public ValidationError(String field, String message) {
        this();
        this.field = field;
        this.message = message;
    }
    
    /**
     * Constructor with field, message, and code.
     */
    public ValidationError(String field, String message, String code) {
        this(field, message);
        this.code = code;
    }
    
    // Getters and Setters
    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public Object getRejectedValue() {
        return rejectedValue;
    }
    
    public void setRejectedValue(Object rejectedValue) {
        this.rejectedValue = rejectedValue;
    }
    
    public Map<String, Object> getDetails() {
        return details;
    }
    
    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
    
    /**
     * Adds a detail entry.
     */
    public void addDetail(String key, Object value) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(key, value);
    }
    
    @Override
    public String toString() {
        return "ValidationError{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                ", code='" + code + '\'' +
                ", rejectedValue=" + rejectedValue +
                ", details=" + details +
                '}';
    }
}
