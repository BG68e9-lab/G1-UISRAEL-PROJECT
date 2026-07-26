package com.uisrael.cwdrinkhouse.exception;

/**
 * Base exception class for all Drinkhouse application exceptions.
 * This is the root exception for the custom exception hierarchy.
 */
public class DrinkHouseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new DrinkHouseException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     */
    public DrinkHouseException(String message) {
        super(message);
    }

    /**
     * Constructs a new DrinkHouseException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method)
     * @param cause   the cause (which is saved for later retrieval by the getCause() method)
     */
    public DrinkHouseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DrinkHouseException with the specified cause.
     *
     * @param cause the cause (which is saved for later retrieval by the getCause() method)
     */
    public DrinkHouseException(Throwable cause) {
        super(cause);
    }
}
