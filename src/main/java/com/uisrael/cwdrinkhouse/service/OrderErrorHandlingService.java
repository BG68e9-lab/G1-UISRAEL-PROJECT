package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.UserFriendlyError;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.TimeoutException;

/**
 * Service interface for handling and transforming order-related exceptions into user-friendly errors.
 * 
 * This service provides comprehensive error handling capabilities that transform technical exceptions
 * into clear, actionable user messages while preserving diagnostic information for support teams.
 * 
 * Key responsibilities:
 * - Transform WebClient exceptions into user-friendly messages
 * - Handle validation errors with field-specific feedback
 * - Process database exceptions with clear explanations
 * - Manage timeout scenarios with retry guidance
 * - Generate correlation IDs for error tracking
 * - Categorize errors by type for proper HTTP status mapping
 * 
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6
 */
public interface OrderErrorHandlingService {

    /**
     * Handles WebClient response exceptions and transforms them into user-friendly errors.
     * 
     * Processes different HTTP status codes from backend API calls and creates appropriate
     * user messages with technical details for troubleshooting.
     * 
     * @param ex the WebClient response exception
     * @return UserFriendlyError with appropriate user message and categorization
     */
    UserFriendlyError handleWebClientException(WebClientResponseException ex);

    /**
     * Handles validation exceptions and creates user-friendly error responses.
     * 
     * Processes field-level validation errors and business rule violations,
     * creating clear messages that guide users toward corrective action.
     * 
     * @param ex the validation exception
     * @return UserFriendlyError with field-specific validation messages
     */
    UserFriendlyError handleValidationException(ValidationException ex);

    /**
     * Handles database exceptions and transforms them into user-friendly errors.
     * 
     * Processes constraint violations, connection issues, and data integrity errors,
     * providing clear explanations without exposing technical database details.
     * 
     * @param ex the data access exception
     * @return UserFriendlyError with database-related user guidance
     */
    UserFriendlyError handleDatabaseException(DataAccessException ex);

    /**
     * Handles timeout exceptions and creates user-friendly error responses.
     * 
     * Processes operation timeouts and provides guidance on retry strategies
     * and potential system load issues.
     * 
     * @param ex the timeout exception
     * @return UserFriendlyError with timeout-specific user guidance and retry information
     */
    UserFriendlyError handleTimeoutException(TimeoutException ex);

    /**
     * Handles generic exceptions that don't fit other categories.
     * 
     * Provides a fallback error handling mechanism for unexpected exceptions
     * while preserving error details for debugging.
     * 
     * @param ex the generic exception
     * @param operationContext context describing what operation was being performed
     * @return UserFriendlyError with generic error message and support information
     */
    UserFriendlyError handleGenericException(Exception ex, String operationContext);

    /**
     * Logs detailed error information with correlation ID for diagnostic purposes.
     * 
     * Creates comprehensive error logs that include stack traces, operation context,
     * and system state information for troubleshooting by support teams.
     * 
     * @param ex the exception to log
     * @param operationContext description of the operation that failed
     * @return correlation ID generated for this error instance
     */
    String logDetailedError(Exception ex, String operationContext);

    /**
     * Generates a unique correlation ID for error tracking.
     * 
     * Creates identifiers that can be used to correlate user-facing error messages
     * with detailed technical logs for support and debugging purposes.
     * 
     * @return unique correlation ID
     */
    String generateCorrelationId();

    /**
     * Categorizes an exception into appropriate error categories.
     * 
     * Analyzes exception types and their characteristics to determine the most
     * appropriate error category for user presentation and system handling.
     * 
     * @param ex the exception to categorize
     * @return the appropriate error category
     */
    UserFriendlyError.ErrorCategory categorizeException(Exception ex);

    /**
     * Determines the appropriate HTTP status code for an exception.
     * 
     * Maps different exception types to their corresponding HTTP status codes
     * for proper REST API error responses.
     * 
     * @param ex the exception to map
     * @return appropriate HTTP status code
     */
    int determineHttpStatusCode(Exception ex);

    /**
     * Adds retry information to error responses for transient failures.
     * 
     * Analyzes exception types to determine if they represent transient failures
     * and adds appropriate retry guidance to the error response.
     * 
     * @param error the user-friendly error to enhance
     * @param ex the original exception
     * @return the enhanced error with retry information
     */
    UserFriendlyError addRetryInformation(UserFriendlyError error, Exception ex);

    /**
     * Adds support information to error responses.
     * 
     * Enhances error responses with contact information and troubleshooting
     * resources to help users resolve issues or get assistance.
     * 
     * @param error the user-friendly error to enhance  
     * @return the enhanced error with support information
     */
    UserFriendlyError addSupportInformation(UserFriendlyError error);
}