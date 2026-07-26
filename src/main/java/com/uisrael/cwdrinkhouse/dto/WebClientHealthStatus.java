package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * WebClient health status for order creation diagnostics.
 * Contains detailed analysis of WebClient configuration, connection pool health,
 * and communication status with backend services.
 * 
 * Requirements: 1.2, 1.6
 */
public class WebClientHealthStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Timestamp when the health check was performed.
     */
    private LocalDateTime timestamp;

    /**
     * Overall WebClient health status.
     */
    private boolean healthy;

    /**
     * Backend connectivity status.
     */
    private boolean backendReachable;

    /**
     * Connection pool health status.
     */
    private boolean connectionPoolHealthy;

    /**
     * Timeout configuration status.
     */
    private boolean timeoutConfigurationValid;

    /**
     * Retry configuration status.
     */
    private boolean retryConfigurationValid;

    /**
     * Connection timeout setting.
     */
    private Duration connectTimeout;

    /**
     * Read timeout setting.
     */
    private Duration readTimeout;

    /**
     * Write timeout setting.
     */
    private Duration writeTimeout;

    /**
     * Maximum retry attempts configured.
     */
    private int maxRetryAttempts;

    /**
     * Retry backoff delay configured.
     */
    private Duration retryBackoffDelay;

    /**
     * Maximum connections in the pool.
     */
    private int maxConnections;

    /**
     * Active connections count.
     */
    private int activeConnections;

    /**
     * Available connections count.
     */
    private int availableConnections;

    /**
     * Connection pool utilization percentage.
     */
    private double connectionPoolUtilization;

    /**
     * Average response time for recent requests.
     */
    private Duration averageResponseTime;

    /**
     * Endpoint availability results.
     */
    private Map<String, EndpointHealthResult> endpointResults;

    /**
     * Additional configuration details.
     */
    private Map<String, Object> configurationDetails;

    /**
     * Health check diagnostic messages.
     */
    private Map<String, String> diagnosticMessages;

    /**
     * Default constructor.
     */
    public WebClientHealthStatus() {
        this.timestamp = LocalDateTime.now();
        this.endpointResults = new HashMap<>();
        this.configurationDetails = new HashMap<>();
        this.diagnosticMessages = new HashMap<>();
    }

    /**
     * Adds an endpoint health result.
     * 
     * @param endpoint the endpoint URL
     * @param result the health check result
     */
    public void addEndpointResult(String endpoint, EndpointHealthResult result) {
        if (endpoint != null && result != null) {
            this.endpointResults.put(endpoint, result);
        }
    }

    /**
     * Adds a configuration detail.
     * 
     * @param key the configuration key
     * @param value the configuration value
     */
    public void addConfigurationDetail(String key, Object value) {
        if (key != null && value != null) {
            this.configurationDetails.put(key, value);
        }
    }

    /**
     * Adds a diagnostic message.
     * 
     * @param category the message category
     * @param message the diagnostic message
     */
    public void addDiagnosticMessage(String category, String message) {
        if (category != null && message != null) {
            this.diagnosticMessages.put(category, message);
        }
    }

    /**
     * Calculates connection pool utilization percentage.
     * 
     * @return utilization percentage (0-100)
     */
    public double calculateUtilization() {
        if (maxConnections > 0) {
            this.connectionPoolUtilization = ((double) activeConnections / maxConnections) * 100.0;
        } else {
            this.connectionPoolUtilization = 0.0;
        }
        return this.connectionPoolUtilization;
    }

    /**
     * Gets the overall health summary.
     * 
     * @return formatted health summary
     */
    public String getHealthSummary() {
        return String.format("WebClient Health: %s, Backend: %s, Pool: %.1f%% utilized (%d/%d connections)",
                healthy ? "HEALTHY" : "UNHEALTHY",
                backendReachable ? "REACHABLE" : "UNREACHABLE",
                connectionPoolUtilization,
                activeConnections,
                maxConnections);
    }

    /**
     * Endpoint health check result.
     */
    public static class EndpointHealthResult implements Serializable {
        private String endpoint;
        private boolean available;
        private int statusCode;
        private Duration responseTime;
        private String errorMessage;
        private LocalDateTime lastChecked;

        public EndpointHealthResult() {
            this.lastChecked = LocalDateTime.now();
        }

        public EndpointHealthResult(String endpoint, boolean available, int statusCode, 
                                   Duration responseTime, String errorMessage) {
            this();
            this.endpoint = endpoint;
            this.available = available;
            this.statusCode = statusCode;
            this.responseTime = responseTime;
            this.errorMessage = errorMessage;
        }

        // Getters and setters
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public boolean isAvailable() { return available; }
        public void setAvailable(boolean available) { this.available = available; }
        
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        
        public Duration getResponseTime() { return responseTime; }
        public void setResponseTime(Duration responseTime) { this.responseTime = responseTime; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public LocalDateTime getLastChecked() { return lastChecked; }
        public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }

        @Override
        public String toString() {
            return "EndpointHealthResult{" +
                    "endpoint='" + endpoint + '\'' +
                    ", available=" + available +
                    ", statusCode=" + statusCode +
                    ", responseTime=" + responseTime +
                    ", errorMessage='" + errorMessage + '\'' +
                    ", lastChecked=" + lastChecked +
                    '}';
        }
    }

    // Main getters and setters

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public boolean isBackendReachable() {
        return backendReachable;
    }

    public void setBackendReachable(boolean backendReachable) {
        this.backendReachable = backendReachable;
    }

    public boolean isConnectionPoolHealthy() {
        return connectionPoolHealthy;
    }

    public void setConnectionPoolHealthy(boolean connectionPoolHealthy) {
        this.connectionPoolHealthy = connectionPoolHealthy;
    }

    public boolean isTimeoutConfigurationValid() {
        return timeoutConfigurationValid;
    }

    public void setTimeoutConfigurationValid(boolean timeoutConfigurationValid) {
        this.timeoutConfigurationValid = timeoutConfigurationValid;
    }

    public boolean isRetryConfigurationValid() {
        return retryConfigurationValid;
    }

    public void setRetryConfigurationValid(boolean retryConfigurationValid) {
        this.retryConfigurationValid = retryConfigurationValid;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public int getMaxRetryAttempts() {
        return maxRetryAttempts;
    }

    public void setMaxRetryAttempts(int maxRetryAttempts) {
        this.maxRetryAttempts = maxRetryAttempts;
    }

    public Duration getRetryBackoffDelay() {
        return retryBackoffDelay;
    }

    public void setRetryBackoffDelay(Duration retryBackoffDelay) {
        this.retryBackoffDelay = retryBackoffDelay;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
        calculateUtilization();
    }

    public int getAvailableConnections() {
        return availableConnections;
    }

    public void setAvailableConnections(int availableConnections) {
        this.availableConnections = availableConnections;
    }

    public double getConnectionPoolUtilization() {
        return connectionPoolUtilization;
    }

    public void setConnectionPoolUtilization(double connectionPoolUtilization) {
        this.connectionPoolUtilization = connectionPoolUtilization;
    }

    public Duration getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(Duration averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Map<String, EndpointHealthResult> getEndpointResults() {
        return endpointResults;
    }

    public void setEndpointResults(Map<String, EndpointHealthResult> endpointResults) {
        this.endpointResults = endpointResults != null ? endpointResults : new HashMap<>();
    }

    public Map<String, Object> getConfigurationDetails() {
        return configurationDetails;
    }

    public void setConfigurationDetails(Map<String, Object> configurationDetails) {
        this.configurationDetails = configurationDetails != null ? configurationDetails : new HashMap<>();
    }

    public Map<String, String> getDiagnosticMessages() {
        return diagnosticMessages;
    }

    public void setDiagnosticMessages(Map<String, String> diagnosticMessages) {
        this.diagnosticMessages = diagnosticMessages != null ? diagnosticMessages : new HashMap<>();
    }

    @Override
    public String toString() {
        return "WebClientHealthStatus{" +
                "timestamp=" + timestamp +
                ", healthy=" + healthy +
                ", backendReachable=" + backendReachable +
                ", connectionPoolHealthy=" + connectionPoolHealthy +
                ", connectionPoolUtilization=" + connectionPoolUtilization +
                ", activeConnections=" + activeConnections +
                ", maxConnections=" + maxConnections +
                ", averageResponseTime=" + averageResponseTime +
                ", endpointResults=" + endpointResults.size() + " endpoints" +
                '}';
    }
}