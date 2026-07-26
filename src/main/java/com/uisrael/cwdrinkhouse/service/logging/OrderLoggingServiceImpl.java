package com.uisrael.cwdrinkhouse.service.logging;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.OrderDetailDTO;
import com.uisrael.cwdrinkhouse.dto.ValidationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of OrderLoggingService providing structured logging for order operations.
 * 
 * Features:
 * - Structured logging with correlation IDs for request tracking
 * - Sensitive data masking for privacy protection
 * - Performance timing for operations
 * - Contextual error logging with diagnostic information
 * - Appropriate log levels (DEBUG, INFO, WARN, ERROR)
 * 
 * Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7
 */
@Service
public class OrderLoggingServiceImpl implements OrderLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(OrderLoggingServiceImpl.class);

    // Patterns for masking sensitive data
    private static final String ID_MASK = "***";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
    private static final String PHONE_PATTERN = "\\d{3}-\\d{3}-\\d{4}";

    @Override
    public void logOrderCreationAttempt(OrderDTO orderData, String userContext, String correlationId) {
        if (logger.isInfoEnabled()) {
            String maskedOrder = maskSensitiveOrderData(orderData);
            logger.info("[{}] Order creation attempt by user: {} | Order: {} | Details count: {}",
                    correlationId,
                    userContext,
                    maskedOrder,
                    orderData.getDetalles() != null ? orderData.getDetalles().size() : 0);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Order creation detailed attempt - Provider: {}, Total: {}, State: {}",
                    correlationId,
                    maskId(String.valueOf(orderData.getProveedorId())),
                    orderData.getTotal(),
                    orderData.getEstado());
        }
    }

    @Override
    public void logOrderCreationSuccess(OrderDTO createdOrder, Duration totalDuration, String correlationId) {
        if (logger.isInfoEnabled()) {
            logger.info("[{}] Order successfully created | Reference Code: {} | Total: {} | Duration: {}ms",
                    correlationId,
                    maskId(createdOrder.getCodigoReferencia()),
                    createdOrder.getTotal(),
                    totalDuration.toMillis());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Order creation success details - State: {}, Details count: {}, Provider: {}",
                    correlationId,
                    createdOrder.getEstado(),
                    createdOrder.getDetalles() != null ? createdOrder.getDetalles().size() : 0,
                    maskId(String.valueOf(createdOrder.getProveedorId())));
        }
    }

    @Override
    public void logValidationFailures(List<ValidationError> errors, String correlationId) {
        if (errors == null || errors.isEmpty()) {
            return;
        }

        logger.warn("[{}] Order validation failed with {} error(s)", correlationId, errors.size());

        if (logger.isDebugEnabled()) {
            String errorDetails = errors.stream()
                    .map(e -> String.format("Field: %s | Error: %s",
                            e.getField(),
                            e.getMessage()))
                    .collect(Collectors.joining(" | "));
            logger.debug("[{}] Validation error details: {}", correlationId, errorDetails);
        }

        if (logger.isWarnEnabled()) {
            String fieldSummary = errors.stream()
                    .map(ValidationError::getField)
                    .collect(Collectors.joining(", "));
            logger.warn("[{}] Invalid fields: {}", correlationId, fieldSummary);
        }
    }

    @Override
    public void logWebClientRequest(String endpoint, HttpMethod method, String correlationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] WebClient request initiated | Method: {} | Endpoint: {}",
                    correlationId,
                    method,
                    maskSensitiveErrorData(endpoint));
        }

        if (logger.isTraceEnabled()) {
            logger.trace("[{}] WebClient request - Full URL: {}", correlationId, maskSensitiveErrorData(endpoint));
        }
    }

    @Override
    public void logWebClientResponse(String endpoint, int statusCode, Duration responseTime, String correlationId) {
        String logLevel = determineResponseLogLevel(statusCode);

        if (statusCode >= 200 && statusCode < 300) {
            // Success responses
            if (logger.isDebugEnabled()) {
                logger.debug("[{}] WebClient response received | Status: {} | Duration: {}ms | Endpoint: {}",
                        correlationId,
                        statusCode,
                        responseTime.toMillis(),
                        maskSensitiveErrorData(endpoint));
            }
        } else if (statusCode >= 400 && statusCode < 500) {
            // Client errors
            if (logger.isWarnEnabled()) {
                logger.warn("[{}] WebClient request failed | Status: {} | Duration: {}ms | Endpoint: {}",
                        correlationId,
                        statusCode,
                        responseTime.toMillis(),
                        maskSensitiveErrorData(endpoint));
            }
        } else if (statusCode >= 500) {
            // Server errors
            if (logger.isErrorEnabled()) {
                logger.error("[{}] WebClient received server error | Status: {} | Duration: {}ms | Endpoint: {}",
                        correlationId,
                        statusCode,
                        responseTime.toMillis(),
                        maskSensitiveErrorData(endpoint));
            }
        }

        // Performance monitoring
        if (responseTime.toMillis() > 5000) {
            logger.warn("[{}] Slow response detected | Duration: {}ms exceeded 5 seconds | Endpoint: {}",
                    correlationId,
                    responseTime.toMillis(),
                    maskSensitiveErrorData(endpoint));
        }
    }

    @Override
    public void logSystemError(Exception exception, String operationContext, String correlationId) {
        if (logger.isErrorEnabled()) {
            logger.error("[{}] System error during operation: {} | Exception: {} | Message: {}",
                    correlationId,
                    operationContext,
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    exception);
        }

        // Log full stack trace at TRACE level
        if (logger.isTraceEnabled()) {
            logger.trace("[{}] Full error context for operation: {}", correlationId, operationContext, exception);
        }

        // Log categorized error information at DEBUG level
        if (logger.isDebugEnabled()) {
            String errorCategory = categorizeException(exception);
            logger.debug("[{}] Error categorized as: {} | Exception type: {} | Operation: {}",
                    correlationId,
                    errorCategory,
                    exception.getClass().getName(),
                    operationContext);
        }
    }

    @Override
    public void logDatabaseOperation(String operationName, Duration duration, String correlationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Database operation completed | Operation: {} | Duration: {}ms",
                    correlationId,
                    operationName,
                    duration.toMillis());
        }

        // Alert on slow database operations
        if (duration.toMillis() > 1000) {
            logger.warn("[{}] Slow database operation detected | Operation: {} | Duration: {}ms exceeded 1 second",
                    correlationId,
                    operationName,
                    duration.toMillis());
        }
    }

    @Override
    public String maskSensitiveOrderData(OrderDTO orderData) {
        if (orderData == null) {
            return "null";
        }

        return String.format(
                "Order(id=%s, provider=%s, state=%s, total=%s, refCode=%s)",
                maskId(String.valueOf(orderData.getOrdenCompraId())),
                maskId(String.valueOf(orderData.getProveedorId())),
                orderData.getEstado(),
                orderData.getTotal(),
                maskId(orderData.getCodigoReferencia())
        );
    }

    @Override
    public String maskSensitiveErrorData(String message) {
        if (message == null) {
            return "null";
        }

        // Mask email addresses
        String masked = message.replaceAll(EMAIL_PATTERN, "***@***.***");

        // Mask phone numbers
        masked = masked.replaceAll(PHONE_PATTERN, "***-***-****");

        // Mask sequences of numbers that might be IDs or sensitive values
        // Only mask if they appear to be IDs (10+ consecutive digits or after "id=" prefix)
        masked = masked.replaceAll("(?:id=|ID:)\\d{10,}", "id=***");

        return masked;
    }

    @Override
    public void logOrderContext(OrderDTO orderData, Map<String, Object> errorContext, String correlationId) {
        if (logger.isDebugEnabled()) {
            logger.debug("[{}] Order context captured for error analysis | Masked Order: {} | Context keys: {}",
                    correlationId,
                    maskSensitiveOrderData(orderData),
                    errorContext != null ? errorContext.keySet() : "none");
        }

        if (logger.isTraceEnabled() && errorContext != null) {
            String contextDetails = errorContext.entrySet().stream()
                    .map(e -> String.format("%s=%s", e.getKey(), maskSensitiveErrorData(String.valueOf(e.getValue()))))
                    .collect(Collectors.joining(", "));
            logger.trace("[{}] Full error context: {}", correlationId, contextDetails);
        }
    }

    @Override
    public String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Determines the appropriate log level for a given HTTP status code.
     * 
     * @param statusCode the HTTP status code
     * @return log level indicator (not used directly, but helpful for understanding)
     */
    private String determineResponseLogLevel(int statusCode) {
        if (statusCode >= 200 && statusCode < 300) {
            return "DEBUG"; // Success
        } else if (statusCode >= 300 && statusCode < 400) {
            return "DEBUG"; // Redirect
        } else if (statusCode >= 400 && statusCode < 500) {
            return "WARN"; // Client error
        } else {
            return "ERROR"; // Server error
        }
    }

    /**
     * Masks an ID value for safe logging.
     * Replaces the value with a mask while preserving the structure.
     * 
     * @param id the ID to mask
     * @return masked ID
     */
    private String maskId(String id) {
        if (id == null || id.isEmpty()) {
            return ID_MASK;
        }
        // Show first and last character for context, mask the middle
        if (id.length() <= 2) {
            return ID_MASK;
        }
        return id.charAt(0) + "***" + id.charAt(id.length() - 1);
    }

    /**
     * Categorizes an exception for error tracking and analysis.
     * 
     * @param exception the exception to categorize
     * @return category name
     */
    private String categorizeException(Exception exception) {
        if (exception == null) {
            return "UNKNOWN";
        }

        String exceptionName = exception.getClass().getSimpleName();

        if (exceptionName.contains("WebClient") || exceptionName.contains("WebClientResponse")) {
            return "COMMUNICATION";
        } else if (exceptionName.contains("Validation")) {
            return "VALIDATION";
        } else if (exceptionName.contains("Database") || exceptionName.contains("DataAccess")) {
            return "DATABASE";
        } else if (exceptionName.contains("Timeout")) {
            return "TIMEOUT";
        } else if (exceptionName.contains("Security") || exceptionName.contains("Authentication")) {
            return "SECURITY";
        } else if (exceptionName.contains("NotFound") || exceptionName.contains("EntityNotFoundException")) {
            return "NOT_FOUND";
        } else if (exceptionName.contains("Conflict") || exceptionName.contains("ConflictException")) {
            return "CONFLICT";
        } else if (exceptionName.contains("BusinessRule")) {
            return "BUSINESS_RULE";
        } else {
            return "SYSTEM";
        }
    }
}
