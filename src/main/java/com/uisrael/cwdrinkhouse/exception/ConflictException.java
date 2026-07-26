package com.uisrael.cwdrinkhouse.exception;

/**
 * Exception thrown when there is a conflict (e.g., duplicate resource, state conflict).
 * Typically maps to HTTP 409 Conflict.
 */
public class ConflictException extends DrinkHouseException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ConflictException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConflictException(String message) {
        super(message);
    }

    /**
     * Constructs a new ConflictException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
