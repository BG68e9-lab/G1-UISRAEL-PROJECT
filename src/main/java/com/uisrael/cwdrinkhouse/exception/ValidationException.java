package com.uisrael.cwdrinkhouse.exception;

/**
 * Exception thrown when input validation fails.
 * Typically maps to HTTP 400 Bad Request.
 */
public class ValidationException extends DrinkHouseException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ValidationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
