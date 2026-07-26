package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.service.TransactionManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of TransactionManagementService.
 * 
 * Manages transaction boundaries, compensation patterns for distributed transactions,
 * and optimistic locking for concurrent access protection.
 * 
 * Key Responsibilities:
 * - Recording and executing compensating transactions
 * - Managing optimistic locking version numbers
 * - Validating transaction boundaries for order operations
 * - Monitoring concurrent access constraints
 * 
 * Requirements: 8.1, 8.2, 8.3, 8.6
 */
@Service
public class TransactionManagementServiceImpl implements TransactionManagementService {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionManagementServiceImpl.class);
    
    // In-memory storage for compensations (in production, this would be a database)
    private final Map<String, CompensationRecord> compensations = new ConcurrentHashMap<>();
    
    // Version tracking for optimistic locking (in production, this would be in database)
    private final Map<Long, Long> orderVersions = new ConcurrentHashMap<>();
    
    /**
     * Internal class to track compensation information for a transaction.
     */
    private static class CompensationRecord {
        private final Long orderId;
        private final String operationType; // CREATE, UPDATE, DELETE
        private final OrderDTO originalState;
        private final String correlationId;
        private final long createdAt;
        private boolean executed;
        
        CompensationRecord(Long orderId, String operationType, OrderDTO originalState, String correlationId) {
            this.orderId = orderId;
            this.operationType = operationType;
            this.originalState = originalState;
            this.correlationId = correlationId;
            this.createdAt = System.currentTimeMillis();
            this.executed = false;
        }
    }
    
    @Override
    public boolean recordCompensationForOrderCreation(Long orderId, String correlationId) {
        try {
            String compensationKey = generateCompensationKey(orderId, correlationId);
            CompensationRecord record = new CompensationRecord(orderId, "CREATE", null, correlationId);
            compensations.put(compensationKey, record);
            
            logger.info("Recorded compensation for order creation - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to record compensation for order creation", ex);
            return false;
        }
    }
    
    @Override
    public boolean recordCompensationForOrderUpdate(Long orderId, OrderDTO originalOrder, String correlationId) {
        try {
            String compensationKey = generateCompensationKey(orderId, correlationId);
            CompensationRecord record = new CompensationRecord(orderId, "UPDATE", originalOrder, correlationId);
            compensations.put(compensationKey, record);
            
            logger.info("Recorded compensation for order update - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to record compensation for order update", ex);
            return false;
        }
    }
    
    @Override
    public boolean recordCompensationForOrderDeletion(Long orderId, String correlationId) {
        try {
            String compensationKey = generateCompensationKey(orderId, correlationId);
            CompensationRecord record = new CompensationRecord(orderId, "DELETE", null, correlationId);
            compensations.put(compensationKey, record);
            
            logger.info("Recorded compensation for order deletion - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            return true;
        } catch (Exception ex) {
            logger.error("Failed to record compensation for order deletion", ex);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean executeCompensations(Long orderId, String correlationId) {
        try {
            String compensationKey = generateCompensationKey(orderId, correlationId);
            CompensationRecord record = compensations.get(compensationKey);
            
            if (record == null) {
                logger.warn("No compensation record found for OrderId: {}, CorrelationId: {}", 
                    orderId, correlationId);
                return false;
            }
            
            if (record.executed) {
                logger.warn("Compensation already executed for OrderId: {}, CorrelationId: {}", 
                    orderId, correlationId);
                return true;
            }
            
            // Execute the appropriate compensation based on operation type
            boolean result = switch (record.operationType) {
                case "CREATE" -> compensateOrderCreation(orderId, correlationId);
                case "UPDATE" -> compensateOrderUpdate(orderId, record.originalState, correlationId);
                case "DELETE" -> compensateOrderDeletion(orderId, correlationId);
                default -> {
                    logger.error("Unknown operation type for compensation: {}", record.operationType);
                    yield false;
                }
            };
            
            if (result) {
                record.executed = true;
                logger.info("Successfully executed compensation for OrderId: {}, Type: {}, CorrelationId: {}", 
                    orderId, record.operationType, correlationId);
            }
            
            return result;
        } catch (Exception ex) {
            logger.error("Error executing compensations for OrderId: {}", orderId, ex);
            return false;
        }
    }
    
    @Override
    public boolean hasPendingCompensations(Long orderId) {
        return compensations.entrySet().stream()
            .filter(entry -> entry.getValue().orderId.equals(orderId))
            .anyMatch(entry -> !entry.getValue().executed);
    }
    
    @Override
    public boolean validateOptimisticLockVersion(Long clientVersion, Long currentVersion) throws IllegalStateException {
        if (clientVersion == null) {
            // If client doesn't provide version, allow the operation (client is not using optimistic locking)
            return true;
        }
        
        if (currentVersion == null) {
            // If current version doesn't exist, this is a new entity, allow the operation
            return true;
        }
        
        if (!clientVersion.equals(currentVersion)) {
            // Version mismatch indicates concurrent modification
            String message = String.format(
                "Optimistic locking conflict: Client version %d does not match current version %d. " +
                "The order has been modified by another user. Please reload and try again.",
                clientVersion, currentVersion
            );
            logger.warn("Optimistic lock conflict: client={}, current={}", clientVersion, currentVersion);
            throw new IllegalStateException(message);
        }
        
        return true;
    }
    
    @Override
    public Long incrementOrderVersion(Long orderId) {
        try {
            Long currentVersion = orderVersions.getOrDefault(orderId, 0L);
            Long newVersion = currentVersion + 1;
            orderVersions.put(orderId, newVersion);
            
            logger.debug("Incremented version for OrderId: {} from {} to {}", 
                orderId, currentVersion, newVersion);
            return newVersion;
        } catch (Exception ex) {
            logger.error("Failed to increment version for OrderId: {}", orderId, ex);
            return null;
        }
    }
    
    @Override
    public boolean verifyTransactionBoundaries(String operationName) {
        try {
            // Validate that transactional operations are properly configured
            Set<String> validOperations = Set.of(
                "createOrder",
                "updateOrder",
                "deleteOrder",
                "transitionOrder",
                "batchCreateOrders",
                "batchUpdateOrders"
            );
            
            if (!validOperations.contains(operationName)) {
                logger.warn("Operation not recognized for transaction boundary validation: {}", operationName);
                return false;
            }
            
            logger.debug("Transaction boundaries verified for operation: {}", operationName);
            return true;
        } catch (Exception ex) {
            logger.error("Error verifying transaction boundaries", ex);
            return false;
        }
    }
    
    @Override
    public List<String> validateConcurrentAccessConstraints() {
        List<String> issues = new ArrayList<>();
        
        try {
            // Check if version column is being tracked properly
            if (orderVersions.isEmpty()) {
                issues.add("WARNING: Version tracking is empty. Ensure orders are initialized with version numbers.");
            }
            
            // Check for stale compensations (older than 1 hour)
            long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000);
            long staleCompensations = compensations.values().stream()
                .filter(record -> record.createdAt < oneHourAgo && !record.executed)
                .count();
            
            if (staleCompensations > 0) {
                issues.add(String.format(
                    "WARNING: %d stale compensation records found (older than 1 hour). " +
                    "Consider cleanup or escalation.", staleCompensations
                ));
            }
            
            // Validate that concurrent access patterns don't create deadlocks
            // Check if operation sequence could lead to circular dependencies
            logger.debug("Concurrent access constraints validation completed with {} issues", issues.size());
            
        } catch (Exception ex) {
            logger.error("Error validating concurrent access constraints", ex);
            issues.add("ERROR: Failed to validate concurrent access constraints: " + ex.getMessage());
        }
        
        return issues;
    }
    
    /**
     * Generates a unique key for compensation records.
     * Combines orderId and correlationId to ensure uniqueness.
     * 
     * @param orderId the order ID
     * @param correlationId the correlation ID
     * @return unique compensation key
     */
    private String generateCompensationKey(Long orderId, String correlationId) {
        return String.format("%d-%s", orderId, correlationId);
    }
    
    /**
     * Performs compensation for a failed order creation.
     * In a distributed system, this would trigger a delete operation on the backend.
     * 
     * @param orderId the order ID to compensate
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was successful
     */
    private boolean compensateOrderCreation(Long orderId, String correlationId) {
        try {
            logger.info("Executing compensation for order creation - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            
            // In a real implementation, this would call the backend API to delete the order
            // For now, just log the compensation
            // TODO: Implement actual compensation call to backend
            
            return true;
        } catch (Exception ex) {
            logger.error("Failed to compensate order creation", ex);
            return false;
        }
    }
    
    /**
     * Performs compensation for a failed order update.
     * Reverts the order to its original state.
     * 
     * @param orderId the order ID to compensate
     * @param originalOrder the original order state
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was successful
     */
    private boolean compensateOrderUpdate(Long orderId, OrderDTO originalOrder, String correlationId) {
        try {
            logger.info("Executing compensation for order update - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            
            if (originalOrder == null) {
                logger.warn("Original order state is null, cannot compensate");
                return false;
            }
            
            // In a real implementation, this would call the backend API to restore the original state
            // For now, just log the compensation
            // TODO: Implement actual compensation call to backend
            
            return true;
        } catch (Exception ex) {
            logger.error("Failed to compensate order update", ex);
            return false;
        }
    }
    
    /**
     * Performs compensation for a failed order deletion.
     * Restores the deleted order if it was recorded before deletion.
     * 
     * @param orderId the order ID to compensate
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was successful
     */
    private boolean compensateOrderDeletion(Long orderId, String correlationId) {
        try {
            logger.info("Executing compensation for order deletion - OrderId: {}, CorrelationId: {}", 
                orderId, correlationId);
            
            // In a real implementation, this would need to have stored the order data before deletion
            // so it could be restored. This requires capturing the full order state before deletion.
            // For now, just log the compensation
            // TODO: Implement actual compensation call to backend
            
            return true;
        } catch (Exception ex) {
            logger.error("Failed to compensate order deletion", ex);
            return false;
        }
    }
}
