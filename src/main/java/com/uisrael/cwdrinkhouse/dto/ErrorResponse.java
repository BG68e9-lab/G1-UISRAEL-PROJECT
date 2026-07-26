package com.uisrael.cwdrinkhouse.dto;

import java.time.LocalDateTime;

/**
 * Standard error response DTO for API errors.
 * Provides consistent error information across all endpoints.
 */
public class ErrorResponse {

    private int status;
    private String message;
    private String errorCode;
    private String path;
    private LocalDateTime timestamp;
    private String details;

    /**
     * Constructs an ErrorResponse with all parameters.
     *
     * @param status    the HTTP status code
     * @param message   the user-friendly error message
     * @param errorCode the error code for programmatic handling
     * @param path      the request path
     * @param timestamp the timestamp when the error occurred
     * @param details   additional error details
     */
    public ErrorResponse(int status, String message, String errorCode, String path, LocalDateTime timestamp, String details) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters and setters

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
