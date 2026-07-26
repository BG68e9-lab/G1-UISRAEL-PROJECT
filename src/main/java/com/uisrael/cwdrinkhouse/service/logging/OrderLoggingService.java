package com.uisrael.cwdrinkhouse.service.logging;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.OrderDetailDTO;
import com.uisrael.cwdrinkhouse.dto.ValidationError;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Service interface for comprehensive order creation logging with structured information.
 * Provides logging capabilities for:
 * - Order creation attempts with user context
 * - Validation failures with field-specific details
 * - WebClient request/response operations with performance metrics
 * - System errors with correlation IDs
 * 
 * All logging includes sensitive data masking to protect privacy.
 * 
 * Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6
 */
public interface OrderLoggingService {

    /**
     * Logs an order creation attempt with user context and order summary.
     * Includes correlation ID, user information, and order details.
     * 
     * @param orderData the order being created
     * @param userContext user identity and session information
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.1 - Log order creation with user context
     */
    void logOrderCreationAttempt(OrderDTO orderData, String userContext, String correlationId);

    /**
     * Logs successful order creation with performance metrics.
     * Includes order ID, reference code, and total duration.
     * 
     * @param createdOrder the successfully created order
     * @param totalDuration time taken for the operation
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.6 - Log success events with order ID and reference code
     */
    void logOrderCreationSuccess(OrderDTO createdOrder, Duration totalDuration, String correlationId);

    /**
     * Logs validation failures with field-specific details.
     * Each error includes field name, invalid value, and error message.
     * 
     * @param errors list of validation errors
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.2 - Log validation failures with field-specific details
     */
    void logValidationFailures(List<ValidationError> errors, String correlationId);

    /**
     * Logs a WebClient HTTP request with endpoint and method information.
     * Called before sending the request.
     * 
     * @param endpoint the target URL
     * @param method HTTP method (GET, POST, etc.)
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.3 - Log request URLs and HTTP methods
     */
    void logWebClientRequest(String endpoint, HttpMethod method, String correlationId);

    /**
     * Logs a WebClient HTTP response with status code and performance metrics.
     * Called after receiving the response.
     * 
     * @param endpoint the target URL
     * @param statusCode HTTP status code
     * @param responseTime duration of the request/response
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.3 - Log response status codes and 7.5 - Log performance metrics
     */
    void logWebClientResponse(String endpoint, int statusCode, Duration responseTime, String correlationId);

    /**
     * Logs a system error with complete diagnostic information.
     * Includes stack trace, error categorization, and correlation ID for support.
     * 
     * @param exception the exception that occurred
     * @param operationContext description of what operation was being performed
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.4 - Log stack traces with correlation IDs
     */
    void logSystemError(Exception exception, String operationContext, String correlationId);

    /**
     * Logs database operation with timing information.
     * Useful for performance monitoring and debugging.
     * 
     * @param operationName description of the database operation
     * @param duration time taken for the operation
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.5 - Log performance metrics for order creation operations
     */
    void logDatabaseOperation(String operationName, Duration duration, String correlationId);

    /**
     * Masks sensitive data in order information for safe logging.
     * Masks IDs, email addresses, and other personal information.
     * 
     * @param orderData the order to mask
     * @return masked order information suitable for logging
     * 
     * Requirement: 7.7 - Ensure sensitive data masking in log outputs
     */
    String maskSensitiveOrderData(OrderDTO orderData);

    /**
     * Masks sensitive data in error messages for safe logging.
     * Prevents exposure of personal information or system internals.
     * 
     * @param message the error message to mask
     * @return masked message suitable for logging
     * 
     * Requirement: 7.7 - Ensure sensitive data masking in log outputs
     */
    String maskSensitiveErrorData(String message);

    /**
     * Extracts and logs order context for error diagnostics.
     * Called when an error occurs to capture the state for analysis.
     * 
     * @param orderData the order being processed
     * @param errorContext additional context about the error
     * @param correlationId unique identifier for request tracking
     * 
     * Requirement: 7.1 - Log order creation attempts with context
     */
    void logOrderContext(OrderDTO orderData, Map<String, Object> errorContext, String correlationId);

    /**
     * Generates a correlation ID for request tracking.
     * Should be called at the start of an operation and stored in MDC.
     * 
     * @return unique correlation ID
     * 
     * Requirement: 7.4 - Correlation IDs for error tracking
     */
    String generateCorrelationId();
}
