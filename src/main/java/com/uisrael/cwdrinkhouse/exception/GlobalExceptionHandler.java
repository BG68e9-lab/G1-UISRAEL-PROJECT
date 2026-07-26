package com.uisrael.cwdrinkhouse.exception;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import com.uisrael.cwdrinkhouse.dto.ErrorResponse;

/**
 * Global exception handler for the Drinkhouse application.
 * Handles all exceptions thrown in the application and maps them to appropriate HTTP status codes
 * with user-friendly error messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles EntityNotFoundException (404 Not Found).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                "ENTITY_NOT_FOUND",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                "The requested resource was not found. Please verify the identifier and try again."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles ValidationException (400 Bad Request).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                "VALIDATION_ERROR",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                "Please check your input and ensure all required fields are valid."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ConflictException (409 Conflict).
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                "CONFLICT",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                "There is a conflict with an existing resource. This could be due to a duplicate entry or incompatible state."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * Handles BusinessRuleException (422 Unprocessable Entity).
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleException(
            BusinessRuleException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                "BUSINESS_RULE_VIOLATION",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                "The operation violates one or more business rules. Please review the error details and adjust your request."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * Handles ExternalServiceException.
     * Maps to appropriate HTTP status based on the service response.
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            ExternalServiceException ex,
            WebRequest request) {
        
        HttpStatus status = determineStatusFromExternalService(ex);
        
        String details = ex.getServiceName() != null && ex.getHttpStatusCode() != null
                ? String.format("External service '%s' error. Status code: %d", 
                    ex.getServiceName(), ex.getHttpStatusCode())
                : "An error occurred while communicating with an external service.";
        
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                "EXTERNAL_SERVICE_ERROR",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                details
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles MethodArgumentNotValidException (400 Bad Request).
     * Provides detailed field validation error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        StringBuilder errorDetails = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errorDetails.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("; ");
            } else {
                errorDetails.append(error.getDefaultMessage()).append("; ");
            }
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed. Please check the input fields.",
                "VALIDATION_ERROR",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                errorDetails.toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all uncaught DrinkHouseException (500 Internal Server Error by default).
     */
    @ExceptionHandler(DrinkHouseException.class)
    public ResponseEntity<ErrorResponse> handleDrinkHouseException(
            DrinkHouseException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An application error occurred. Please try again later.",
                "APPLICATION_ERROR",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all uncaught exceptions (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please contact support if the problem persists.",
                "INTERNAL_SERVER_ERROR",
                request.getDescription(false).replace("uri=", ""),
                LocalDateTime.now(),
                ex.getClass().getName() + ": " + ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Determines the appropriate HTTP status based on the external service exception.
     *
     * @param ex the ExternalServiceException
     * @return the appropriate HttpStatus
     */
    private HttpStatus determineStatusFromExternalService(ExternalServiceException ex) {
        if (ex.getHttpStatusCode() == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return switch (ex.getHttpStatusCode()) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 409 -> HttpStatus.CONFLICT;
            case 422 -> HttpStatus.UNPROCESSABLE_ENTITY;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            case 500 -> HttpStatus.INTERNAL_SERVER_ERROR;
            case 502 -> HttpStatus.BAD_GATEWAY;
            case 503 -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
