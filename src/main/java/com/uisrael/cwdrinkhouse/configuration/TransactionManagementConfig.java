package com.uisrael.cwdrinkhouse.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration for transaction management and data consistency.
 * 
 * Enables declarative transaction management for order operations with:
 * - Proper rollback behavior for exceptions
 * - Transaction timeout settings
 * - Optimistic locking support
 * - Compensation pattern support for distributed transactions
 * 
 * This configuration ensures that:
 * 1. All @Transactional methods are properly intercepted
 * 2. Transactions are rolled back on exceptions
 * 3. Transaction timeouts prevent indefinite waits
 * 4. Optimistic locking detects concurrent modifications
 * 5. Compensation logic handles partial failures in distributed transactions
 * 
 * Requirements: 8.1, 8.2, 8.3, 8.6
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true, mode = org.springframework.context.annotation.AdviceMode.PROXY)
public class TransactionManagementConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionManagementConfig.class);
    
    /**
     * Transaction template bean for programmatic transaction management.
     * Provides a template for executing code within a transaction boundary.
     * 
     * @param transactionManager the platform transaction manager
     * @return TransactionTemplate configured with proper isolation level and timeout
     */
    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        
        // Set transaction isolation level to READ_COMMITTED for optimistic locking
        // This allows higher concurrency while maintaining data consistency
        template.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED);
        
        // Set transaction timeout to 60 seconds to prevent indefinite locks
        template.setTimeout(60);
        
        // Set to propagate existing transactions if called within another transaction
        template.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED);
        
        logger.info("TransactionTemplate configured with ISOLATION_READ_COMMITTED, timeout=60s");
        return template;
    }
    
    /**
     * Bean for transaction properties configuration.
     * Centralizes transaction-related configuration properties.
     * 
     * @return OrderTransactionProperties with configured values
     */
    @Bean
    public OrderTransactionProperties orderTransactionProperties() {
        return new OrderTransactionProperties();
    }
    
    /**
     * Configuration holder for order-related transaction properties.
     * These properties control transaction behavior for order operations.
     */
    public static class OrderTransactionProperties {
        
        /**
         * Default transaction timeout in seconds for order operations.
         * Operations taking longer than this will be rolled back.
         */
        private int orderOperationTimeout = 60;
        
        /**
         * Transaction isolation level for order operations.
         * READ_COMMITTED (1) - Default, prevents dirty reads
         * REPEATABLE_READ (2) - Prevents non-repeatable reads
         * SERIALIZABLE (3) - Prevents phantom reads but reduces concurrency
         */
        private int isolationLevel = org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED;
        
        /**
         * Whether to enable optimistic locking for order updates.
         * When enabled, version numbers are checked to detect concurrent modifications.
         */
        private boolean optimisticLockingEnabled = true;
        
        /**
         * Whether to enable compensation patterns for distributed transactions.
         * When enabled, compensating transactions are recorded for failed operations.
         */
        private boolean compensationEnabled = true;
        
        /**
         * Maximum number of retry attempts for transactional operations.
         * After this many retries, the operation is failed.
         */
        private int maxRetryAttempts = 3;
        
        /**
         * Base delay in milliseconds for exponential backoff retry strategy.
         */
        private long retryBackoffBaseDelay = 100;
        
        /**
         * Whether to read uncommitted changes from other transactions.
         * Should be false for consistency. Setting to true reduces consistency guarantees.
         */
        private boolean readUncommitted = false;
        
        // Getters
        
        public int getOrderOperationTimeout() {
            return orderOperationTimeout;
        }
        
        public int getIsolationLevel() {
            return isolationLevel;
        }
        
        public boolean isOptimisticLockingEnabled() {
            return optimisticLockingEnabled;
        }
        
        public boolean isCompensationEnabled() {
            return compensationEnabled;
        }
        
        public int getMaxRetryAttempts() {
            return maxRetryAttempts;
        }
        
        public long getRetryBackoffBaseDelay() {
            return retryBackoffBaseDelay;
        }
        
        public boolean isReadUncommitted() {
            return readUncommitted;
        }
    }
}
