package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.ValidationResult;

import java.util.Map;

/**
 * Service for validating database constraints before persistence operations.
 * Checks foreign key integrity, unique constraints, and provides detailed error reporting.
 * 
 * Requirements: 5.1, 5.2, 8.4, 8.5
 */
public interface DatabaseConstraintValidationService {
    
    /**
     * Validates an order against database constraints.
     * 
     * @param order the order to validate
     * @return ValidationResult with all constraint violations found
     */
    ValidationResult validateOrder(OrderDTO order);
    
    /**
     * Validates provider existence and relationships.
     * 
     * @param providerId the provider ID to validate
     * @return ValidationResult with any constraint violations
     */
    ValidationResult validateProviderConstraints(Long providerId);
    
    /**
     * Validates product existence and inventory constraints.
     * 
     * @param productId the product ID to validate
     * @return ValidationResult with any constraint violations
     */
    ValidationResult validateProductConstraints(Long productId);
    
    /**
     * Creates a constraint violation error with details.
     * 
     * @param constraintType the type of constraint violated
     * @param field the field involved
     * @param details additional details about the violation
     * @return ValidationResult containing the error information
     */
    ValidationResult createConstraintViolationError(String constraintType, String field, Map<String, Object> details);
}
