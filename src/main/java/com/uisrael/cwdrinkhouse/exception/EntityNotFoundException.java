package com.uisrael.cwdrinkhouse.exception;

/**
 * Exception thrown when a requested entity is not found.
 * Typically maps to HTTP 404 Not Found.
 */
public class EntityNotFoundException extends DrinkHouseException {

    private static final long serialVersionUID = 1L;

    private String entityName;
    private String identifier;

    /**
     * Constructs a new EntityNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntityNotFoundException with entity name and identifier.
     *
     * @param entityName the name of the entity (e.g., "Producto")
     * @param identifier the identifier of the entity (e.g., "123")
     */
    public EntityNotFoundException(String entityName, String identifier) {
        super(String.format("%s with identifier '%s' not found", entityName, identifier));
        this.entityName = entityName;
        this.identifier = identifier;
    }

    /**
     * Constructs a new EntityNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getEntityName() {
        return entityName;
    }

    public String getIdentifier() {
        return identifier;
    }
}
