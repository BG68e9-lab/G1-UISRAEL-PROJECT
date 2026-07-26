package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing transaction boundaries and compensation patterns.
 * Handles distributed transaction scenarios and provides compensation methods
 * for partial failures in multi-step operations.
 * 
 * Compensating transactions are used to undo the effects of a committed transaction
 * when subsequent operations in a distributed transaction fail, ensuring eventual
 * consistency across services.
 * 
 * Requirements: 8.1, 8.2, 8.3, 8.6
 */
public interface TransactionManagementService {
    
    /**
     * Records a compensating transaction for an order creation that failed downstream.
     * This allows the system to rollback the order creation if backend operations fail.
     * 
     * @param orderId the order ID that needs compensation
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was recorded successfully
     */
    boolean recordCompensationForOrderCreation(Long orderId, String correlationId);
    
    /**
     * Records a compensating transaction for an order update that failed downstream.
     * Stores the original state to allow rollback if needed.
     * 
     * @param orderId the order ID that needs compensation
     * @param originalOrder the original order state before update
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was recorded successfully
     */
    boolean recordCompensationForOrderUpdate(Long orderId, OrderDTO originalOrder, String correlationId);
    
    /**
     * Records a compensating transaction for an order deletion that failed downstream.
     * 
     * @param orderId the order ID that needs compensation
     * @param correlationId the correlation ID for tracking
     * @return true if compensation was recorded successfully
     */
    boolean recordCompensationForOrderDeletion(Long orderId, String correlationId);
    
    /**
     * Executes pending compensations for a transaction that has failed.
     * This method is called when a transactional operation encounters an error
     * after changes have been committed, requiring rollback of downstream changes.
     * 
     * @param orderId the order ID to compensate
     * @param correlationId the correlation ID for tracking
     * @return true if all compensations were executed successfully
     */
    @Transactional
    boolean executeCompensations(Long orderId, String correlationId);
    
    /**
     * Checks if an order has pending compensations due to failed operations.
     * 
     * @param orderId the order ID to check
     * @return true if compensations are pending
     */
    boolean hasPendingCompensations(Long orderId);
    
    /**
     * Validates optimistic locking version conflict.
     * Throws an exception if the client's version doesn't match the current version.
     * 
     * @param clientVersion the version from the client
     * @param currentVersion the current version in the system
     * @return true if versions match (no conflict)
     * @throws IllegalStateException if version conflict is detected
     */
    boolean validateOptimisticLockVersion(Long clientVersion, Long currentVersion) throws IllegalStateException;
    
    /**
     * Increments the version number for an order after successful update.
     * This ensures optimistic locking detects concurrent modifications.
     * 
     * @param orderId the order ID
     * @return the new version number
     */
    Long incrementOrderVersion(Long orderId);
    
    /**
     * Verifies transaction boundaries for order operations.
     * Ensures that all related operations are properly grouped within transactions.
     * 
     * @param operationName the name of the operation to verify
     * @return true if transaction boundaries are properly configured
     */
    boolean verifyTransactionBoundaries(String operationName);
    
    /**
     * Checks database constraint compatibility with concurrent access patterns.
     * Ensures foreign key constraints and unique constraints are properly indexed
     * for optimistic locking to work efficiently.
     * 
     * @return list of any configuration issues found
     */
    List<String> validateConcurrentAccessConstraints();
}
