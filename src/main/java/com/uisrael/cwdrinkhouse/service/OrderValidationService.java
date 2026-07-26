package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.OrderDetailDTO;
import com.uisrael.cwdrinkhouse.dto.ValidationResult;

import java.util.List;

/**
 * Service interface for comprehensive order validation operations.
 * Provides validation methods for order data integrity, business rules enforcement,
 * and database referential integrity checks.
 * 
 * This service validates orders before creation/update to ensure:
 * - Data integrity and format validation
 * - Business rule compliance
 * - Provider and product existence validation
 * - Quantity and price validation
 * - Order detail line item validation
 * - Total calculation validation
 * 
 * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.7
 */
public interface OrderValidationService {

    /**
     * Validates complete order data for creation or update operations.
     * Performs comprehensive validation including:
     * - Basic field validation (required fields, data types)
     * - Business rule validation 
     * - Provider existence validation
     * - Product existence validation for all order details
     * - Quantity and price validation
     * - Total calculation validation
     * 
     * @param orderData the order data to validate
     * @return validation result with detailed error information
     * 
     * Requirements: 5.1, 5.2, 5.3, 5.4, 5.5, 5.7
     */
    ValidationResult validateOrderData(OrderDTO orderData);

    /**
     * Validates order details (line items) for completeness and integrity.
     * Checks:
     * - At least one detail line exists
     * - All product IDs are valid and reference existing products
     * - Quantities are positive numbers
     * - Unit prices are positive numbers
     * - Subtotal calculations are correct
     * 
     * @param details the list of order details to validate
     * @return validation result with field-specific errors
     * 
     * Requirements: 5.2, 5.3, 5.4, 5.5
     */
    ValidationResult validateOrderDetails(List<OrderDetailDTO> details);

    /**
     * Validates that a provider ID references an existing active provider.
     * Performs database/backend validation to ensure:
     * - Provider ID is not null and positive
     * - Provider exists in the system
     * - Provider is in active status
     * 
     * @param providerId the provider ID to validate
     * @return validation result indicating provider existence and status
     * 
     * Requirements: 5.1
     */
    ValidationResult validateProviderExists(Long providerId);

    /**
     * Validates that product IDs reference existing products.
     * Performs batch validation against the backend to ensure:
     * - All product IDs are not null and positive
     * - All products exist in the system
     * - Products are available for ordering
     * 
     * @param productIds the list of product IDs to validate
     * @return validation result with details for each invalid product ID
     * 
     * Requirements: 5.2
     */
    ValidationResult validateProductsExist(List<Long> productIds);

    /**
     * Validates business rules for order creation and updates.
     * Enforces business logic including:
     * - Order must be in valid state for the operation
     * - Provider must be active and available
     * - Order total must match calculated total from details
     * - Quantities must be within acceptable limits
     * - Order reference code format and uniqueness
     * - Business-specific constraints
     * 
     * @param orderData the order data to validate against business rules
     * @return validation result with business rule violation details
     * 
     * Requirements: 5.7
     */
    ValidationResult validateBusinessRules(OrderDTO orderData);

    /**
     * Validates quantity values in order details.
     * Ensures:
     * - Quantities are not null
     * - Quantities are positive integers
     * - Quantities are within acceptable business limits (e.g., max 999,999)
     * 
     * @param details the order details to validate quantities for
     * @return validation result with quantity-specific errors
     * 
     * Requirements: 5.3
     */
    ValidationResult validateQuantities(List<OrderDetailDTO> details);

    /**
     * Validates unit price values in order details.
     * Ensures:
     * - Unit prices are not null
     * - Unit prices are positive decimal values
     * - Unit prices have appropriate precision (max 2 decimal places)
     * - Unit prices are within reasonable business limits
     * 
     * @param details the order details to validate prices for
     * @return validation result with price-specific errors
     * 
     * Requirements: 5.4
     */
    ValidationResult validateUnitPrices(List<OrderDetailDTO> details);

    /**
     * Validates that the order total matches the calculated total from details.
     * Ensures:
     * - Order total is not null
     * - Calculated total from details matches stored total
     * - Calculation precision is maintained (2 decimal places)
     * 
     * @param orderData the order data with total and details
     * @return validation result indicating total calculation accuracy
     * 
     * Requirements: 5.5
     */
    ValidationResult validateTotalCalculation(OrderDTO orderData);

    /**
     * Validates that order has at least one detail line.
     * Ensures business rule that orders cannot be empty.
     * 
     * @param orderData the order data to check for details
     * @return validation result indicating presence of order details
     * 
     * Requirements: 5.5
     */
    ValidationResult validateMinimumOrderDetails(OrderDTO orderData);

    /**
     * Performs quick validation check for order data without external calls.
     * Validates only local data integrity:
     * - Required fields presence
     * - Data type validation
     * - Format validation
     * - Basic business rule validation
     * 
     * Does NOT validate:
     * - Provider existence (requires backend call)
     * - Product existence (requires backend call)
     * 
     * @param orderData the order data to validate
     * @return validation result with local validation errors only
     */
    ValidationResult validateOrderDataLocally(OrderDTO orderData);

    /**
     * Validates order state and determines if order can be modified.
     * Checks:
     * - Order state is valid
     * - Order state allows the intended operation
     * - State transitions are permitted
     * 
     * @param currentState the current order state
     * @param intendedOperation the intended operation (CREATE, UPDATE, DELETE, TRANSITION)
     * @return validation result indicating if operation is permitted
     */
    ValidationResult validateOrderStateForOperation(String currentState, String intendedOperation);
}