package com.uisrael.cwdrinkhouse.exception;

/**
 * Exception thrown when an external service call fails.
 * May map to various HTTP status codes depending on the root cause.
 */
public class ExternalServiceException extends DrinkHouseException {

    private static final long serialVersionUID = 1L;

    private String serviceName;
    private Integer httpStatusCode;

    /**
     * Constructs a new ExternalServiceException with the specified detail message.
     *
     * @param message the detail message
     */
    public ExternalServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new ExternalServiceException with service name and HTTP status code.
     *
     * @param serviceName    the name of the external service
     * @param httpStatusCode the HTTP status code returned by the service
     * @param message        the detail message
     */
    public ExternalServiceException(String serviceName, Integer httpStatusCode, String message) {
        super(String.format("External service '%s' returned status %d: %s", serviceName, httpStatusCode, message));
        this.serviceName = serviceName;
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * Constructs a new ExternalServiceException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getServiceName() {
        return serviceName;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }
}
