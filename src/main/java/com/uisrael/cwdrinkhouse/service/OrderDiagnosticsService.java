package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.DiagnosticReport;
import com.uisrael.cwdrinkhouse.dto.DatabaseDiagnosticResult;
import com.uisrael.cwdrinkhouse.dto.WebClientHealthStatus;

/**
 * Service interface for systematic order creation failure diagnosis.
 * Provides comprehensive error analysis and root cause identification 
 * for 500 Internal Server Error issues in order creation.
 * 
 * Requirements: 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
 */
public interface OrderDiagnosticsService {

    /**
     * Performs comprehensive diagnosis of order creation failures.
     * Captures complete error stack traces and identifies failure points
     * in the component chain (controller → service → repository → database).
     * 
     * @param orderData the order data that failed to be created
     * @param exception the exception that occurred during creation
     * @return detailed diagnostic report with root cause analysis
     */
    DiagnosticReport diagnoseOrderCreationFailure(OrderDTO orderData, Exception exception);

    /**
     * Validates backend API connectivity and endpoint availability.
     * Checks if the backend service is reachable and responding correctly.
     * 
     * @throws RuntimeException if backend connectivity validation fails
     */
    void validateBackendConnectivity();

    /**
     * Validates the structure and format of order payload data.
     * Checks data types, required fields, and payload structure integrity.
     * 
     * @param orderData the order data to validate
     * @throws IllegalArgumentException if payload structure is invalid
     */
    void validateOrderPayloadStructure(OrderDTO orderData);

    /**
     * Validates database constraints and referential integrity for order creation.
     * Checks foreign key relationships, table constraints, and database health.
     * 
     * @param orderData the order data to validate against database constraints
     * @return detailed database diagnostic result with constraint validation status
     */
    DatabaseDiagnosticResult validateDatabaseConstraints(OrderDTO orderData);

    /**
     * Checks WebClient health status and configuration.
     * Examines connection pool health, timeout settings, and retry configuration.
     * 
     * @return WebClient health status with detailed configuration analysis
     */
    WebClientHealthStatus checkWebClientHealth();

    /**
     * Performs systematic validation of order payload structure.
     * Validates request data types, required field presence, and format compliance.
     * 
     * @param orderData the order data to validate
     * @return true if payload structure is valid
     */
    boolean isPayloadStructureValid(OrderDTO orderData);

    /**
     * Identifies the specific failure point in the component chain.
     * Analyzes stack traces to determine if failure occurred in controller,
     * service, repository, or database layer.
     * 
     * @param exception the exception to analyze
     * @return the component layer where failure occurred
     */
    String identifyFailurePoint(Exception exception);

    /**
     * Captures and formats complete error stack trace information.
     * Provides detailed technical information for debugging purposes.
     * 
     * @param exception the exception to capture
     * @return formatted stack trace with error details
     */
    String captureCompleteStackTrace(Exception exception);

    /**
     * Validates if backend API endpoints are available and responding.
     * Tests connectivity to critical order management endpoints.
     * 
     * @return true if backend endpoints are available
     */
    boolean areBackendEndpointsAvailable();

    /**
     * Checks database connection health and constraint integrity.
     * Validates foreign key relationships and table accessibility.
     * 
     * @return true if database is healthy and accessible
     */
    boolean isDatabaseHealthy();
}