package com.uisrael.cwdrinkhouse.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database diagnostic result for order creation constraint validation.
 * Contains detailed analysis of database health, constraint violations,
 * and foreign key relationship validation.
 * 
 * Requirements: 1.3, 1.4, 1.5
 */
public class DatabaseDiagnosticResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Timestamp when the database diagnostic was performed.
     */
    private LocalDateTime timestamp;

    /**
     * Overall database health status.
     */
    private boolean databaseHealthy;

    /**
     * Connection pool health status.
     */
    private boolean connectionPoolHealthy;

    /**
     * Provider reference validation result.
     */
    private boolean providerExists;

    /**
     * Product references validation results.
     */
    private Map<Long, Boolean> productExistenceResults;

    /**
     * Constraint violation details.
     */
    private List<ConstraintViolation> constraintViolations;

    /**
     * Foreign key validation results.
     */
    private List<ForeignKeyValidation> foreignKeyValidations;

    /**
     * Database connectivity metrics.
     */
    private DatabaseMetrics metrics;

    /**
     * Table lock information.
     */
    private List<TableLockInfo> tableLockInfo;

    /**
     * Additional diagnostic messages.
     */
    private List<String> diagnosticMessages;

    /**
     * Default constructor.
     */
    public DatabaseDiagnosticResult() {
        this.timestamp = LocalDateTime.now();
        this.productExistenceResults = new HashMap<>();
        this.constraintViolations = new ArrayList<>();
        this.foreignKeyValidations = new ArrayList<>();
        this.tableLockInfo = new ArrayList<>();
        this.diagnosticMessages = new ArrayList<>();
        this.metrics = new DatabaseMetrics();
    }

    /**
     * Adds a constraint violation to the diagnostic result.
     * 
     * @param violation the constraint violation to add
     */
    public void addConstraintViolation(ConstraintViolation violation) {
        if (violation != null) {
            this.constraintViolations.add(violation);
        }
    }

    /**
     * Adds a foreign key validation result.
     * 
     * @param validation the foreign key validation result
     */
    public void addForeignKeyValidation(ForeignKeyValidation validation) {
        if (validation != null) {
            this.foreignKeyValidations.add(validation);
        }
    }

    /**
     * Adds a diagnostic message.
     * 
     * @param message the diagnostic message
     */
    public void addDiagnosticMessage(String message) {
        if (message != null && !message.trim().isEmpty()) {
            this.diagnosticMessages.add(message);
        }
    }

    /**
     * Sets product existence result for a specific product ID.
     * 
     * @param productId the product ID
     * @param exists whether the product exists
     */
    public void setProductExists(Long productId, boolean exists) {
        this.productExistenceResults.put(productId, exists);
    }

    /**
     * Gets the overall validation result.
     * 
     * @return true if all validations passed
     */
    public boolean isValid() {
        return databaseHealthy && 
               connectionPoolHealthy && 
               providerExists && 
               constraintViolations.isEmpty() &&
               productExistenceResults.values().stream().allMatch(Boolean::booleanValue);
    }

    /**
     * Constraint violation information.
     */
    public static class ConstraintViolation implements Serializable {
        private String constraintName;
        private String tableName;
        private String columnName;
        private String violationType;
        private String description;

        public ConstraintViolation() {}

        public ConstraintViolation(String constraintName, String tableName, 
                                 String columnName, String violationType, String description) {
            this.constraintName = constraintName;
            this.tableName = tableName;
            this.columnName = columnName;
            this.violationType = violationType;
            this.description = description;
        }

        // Getters and setters
        public String getConstraintName() { return constraintName; }
        public void setConstraintName(String constraintName) { this.constraintName = constraintName; }
        
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public String getColumnName() { return columnName; }
        public void setColumnName(String columnName) { this.columnName = columnName; }
        
        public String getViolationType() { return violationType; }
        public void setViolationType(String violationType) { this.violationType = violationType; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        @Override
        public String toString() {
            return "ConstraintViolation{" +
                    "constraintName='" + constraintName + '\'' +
                    ", tableName='" + tableName + '\'' +
                    ", columnName='" + columnName + '\'' +
                    ", violationType='" + violationType + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    /**
     * Foreign key validation information.
     */
    public static class ForeignKeyValidation implements Serializable {
        private String foreignKeyName;
        private String referencedTable;
        private Long referencedId;
        private boolean exists;
        private String validationMessage;

        public ForeignKeyValidation() {}

        public ForeignKeyValidation(String foreignKeyName, String referencedTable, 
                                   Long referencedId, boolean exists, String validationMessage) {
            this.foreignKeyName = foreignKeyName;
            this.referencedTable = referencedTable;
            this.referencedId = referencedId;
            this.exists = exists;
            this.validationMessage = validationMessage;
        }

        // Getters and setters
        public String getForeignKeyName() { return foreignKeyName; }
        public void setForeignKeyName(String foreignKeyName) { this.foreignKeyName = foreignKeyName; }
        
        public String getReferencedTable() { return referencedTable; }
        public void setReferencedTable(String referencedTable) { this.referencedTable = referencedTable; }
        
        public Long getReferencedId() { return referencedId; }
        public void setReferencedId(Long referencedId) { this.referencedId = referencedId; }
        
        public boolean isExists() { return exists; }
        public void setExists(boolean exists) { this.exists = exists; }
        
        public String getValidationMessage() { return validationMessage; }
        public void setValidationMessage(String validationMessage) { this.validationMessage = validationMessage; }

        @Override
        public String toString() {
            return "ForeignKeyValidation{" +
                    "foreignKeyName='" + foreignKeyName + '\'' +
                    ", referencedTable='" + referencedTable + '\'' +
                    ", referencedId=" + referencedId +
                    ", exists=" + exists +
                    ", validationMessage='" + validationMessage + '\'' +
                    '}';
        }
    }

    /**
     * Database metrics information.
     */
    public static class DatabaseMetrics implements Serializable {
        private int activeConnections;
        private int maxConnections;
        private long connectionPoolUtilization;
        private long averageResponseTime;
        private boolean connectionPoolExhausted;

        public DatabaseMetrics() {}

        // Getters and setters
        public int getActiveConnections() { return activeConnections; }
        public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }
        
        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }
        
        public long getConnectionPoolUtilization() { return connectionPoolUtilization; }
        public void setConnectionPoolUtilization(long connectionPoolUtilization) { this.connectionPoolUtilization = connectionPoolUtilization; }
        
        public long getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(long averageResponseTime) { this.averageResponseTime = averageResponseTime; }
        
        public boolean isConnectionPoolExhausted() { return connectionPoolExhausted; }
        public void setConnectionPoolExhausted(boolean connectionPoolExhausted) { this.connectionPoolExhausted = connectionPoolExhausted; }

        @Override
        public String toString() {
            return "DatabaseMetrics{" +
                    "activeConnections=" + activeConnections +
                    ", maxConnections=" + maxConnections +
                    ", connectionPoolUtilization=" + connectionPoolUtilization +
                    ", averageResponseTime=" + averageResponseTime +
                    ", connectionPoolExhausted=" + connectionPoolExhausted +
                    '}';
        }
    }

    /**
     * Table lock information.
     */
    public static class TableLockInfo implements Serializable {
        private String tableName;
        private String lockType;
        private String lockMode;
        private boolean isBlocked;
        private String lockingSession;

        public TableLockInfo() {}

        public TableLockInfo(String tableName, String lockType, String lockMode, 
                           boolean isBlocked, String lockingSession) {
            this.tableName = tableName;
            this.lockType = lockType;
            this.lockMode = lockMode;
            this.isBlocked = isBlocked;
            this.lockingSession = lockingSession;
        }

        // Getters and setters
        public String getTableName() { return tableName; }
        public void setTableName(String tableName) { this.tableName = tableName; }
        
        public String getLockType() { return lockType; }
        public void setLockType(String lockType) { this.lockType = lockType; }
        
        public String getLockMode() { return lockMode; }
        public void setLockMode(String lockMode) { this.lockMode = lockMode; }
        
        public boolean isBlocked() { return isBlocked; }
        public void setBlocked(boolean blocked) { isBlocked = blocked; }
        
        public String getLockingSession() { return lockingSession; }
        public void setLockingSession(String lockingSession) { this.lockingSession = lockingSession; }

        @Override
        public String toString() {
            return "TableLockInfo{" +
                    "tableName='" + tableName + '\'' +
                    ", lockType='" + lockType + '\'' +
                    ", lockMode='" + lockMode + '\'' +
                    ", isBlocked=" + isBlocked +
                    ", lockingSession='" + lockingSession + '\'' +
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

    public boolean isDatabaseHealthy() {
        return databaseHealthy;
    }

    public void setDatabaseHealthy(boolean databaseHealthy) {
        this.databaseHealthy = databaseHealthy;
    }

    public boolean isConnectionPoolHealthy() {
        return connectionPoolHealthy;
    }

    public void setConnectionPoolHealthy(boolean connectionPoolHealthy) {
        this.connectionPoolHealthy = connectionPoolHealthy;
    }

    public boolean isProviderExists() {
        return providerExists;
    }

    public void setProviderExists(boolean providerExists) {
        this.providerExists = providerExists;
    }

    public Map<Long, Boolean> getProductExistenceResults() {
        return productExistenceResults;
    }

    public void setProductExistenceResults(Map<Long, Boolean> productExistenceResults) {
        this.productExistenceResults = productExistenceResults != null ? productExistenceResults : new HashMap<>();
    }

    public List<ConstraintViolation> getConstraintViolations() {
        return constraintViolations;
    }

    public void setConstraintViolations(List<ConstraintViolation> constraintViolations) {
        this.constraintViolations = constraintViolations != null ? constraintViolations : new ArrayList<>();
    }

    public List<ForeignKeyValidation> getForeignKeyValidations() {
        return foreignKeyValidations;
    }

    public void setForeignKeyValidations(List<ForeignKeyValidation> foreignKeyValidations) {
        this.foreignKeyValidations = foreignKeyValidations != null ? foreignKeyValidations : new ArrayList<>();
    }

    public DatabaseMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(DatabaseMetrics metrics) {
        this.metrics = metrics;
    }

    public List<TableLockInfo> getTableLockInfo() {
        return tableLockInfo;
    }

    public void setTableLockInfo(List<TableLockInfo> tableLockInfo) {
        this.tableLockInfo = tableLockInfo != null ? tableLockInfo : new ArrayList<>();
    }

    public List<String> getDiagnosticMessages() {
        return diagnosticMessages;
    }

    public void setDiagnosticMessages(List<String> diagnosticMessages) {
        this.diagnosticMessages = diagnosticMessages != null ? diagnosticMessages : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "DatabaseDiagnosticResult{" +
                "timestamp=" + timestamp +
                ", databaseHealthy=" + databaseHealthy +
                ", connectionPoolHealthy=" + connectionPoolHealthy +
                ", providerExists=" + providerExists +
                ", productExistenceResults=" + productExistenceResults.size() + " products" +
                ", constraintViolations=" + constraintViolations.size() + " violations" +
                ", foreignKeyValidations=" + foreignKeyValidations.size() + " validations" +
                ", tableLockInfo=" + tableLockInfo.size() + " locks" +
                ", diagnosticMessages=" + diagnosticMessages.size() + " messages" +
                '}';
    }
}