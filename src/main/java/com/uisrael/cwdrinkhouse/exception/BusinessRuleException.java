package com.uisrael.cwdrinkhouse.exception;

/**
 * Exception thrown when a business rule is violated.
 * Typically maps to HTTP 422 Unprocessable Entity.
 */
public class BusinessRuleException extends DrinkHouseException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BusinessRuleException with the specified detail message.
     *
     * @param message the detail message
     */
    public BusinessRuleException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessRuleException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
