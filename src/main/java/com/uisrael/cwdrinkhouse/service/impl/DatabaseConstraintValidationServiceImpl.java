package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.ValidationResult;
import com.uisrael.cwdrinkhouse.service.DatabaseConstraintValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of DatabaseConstraintValidationService.
 * 
 * Validates database constraints before persistence operations and provides
 * clear error messages for constraint violations.
 * 
 * Requirements: 5.1, 5.2, 8.4, 8.5
 */
@Service
public class DatabaseConstraintValidationServiceImpl implements DatabaseConstraintValidationService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConstraintValidationServiceImpl.class);

    @Override
    public ValidationResult validateOrder(OrderDTO order) {
        logger.debug("Validating order against database constraints");
        ValidationResult result = new ValidationResult();
        
        if (order == null) {
            result.addError("order", "Los datos de la orden son obligatorios", "NULL_ORDER_DATA");
        }
        
        result.setValid(result.getErrors().isEmpty());
        return result;
    }

    @Override
    public ValidationResult validateProviderConstraints(Long providerId) {
        logger.debug("Validating provider constraints: providerId={}", providerId);
        ValidationResult result = new ValidationResult();
        
        if (providerId == null || providerId <= 0) {
            result.addError("providerId", "El ID del proveedor es obligatorio", "INVALID_PROVIDER_ID");
        }
        
        result.setValid(result.getErrors().isEmpty());
        return result;
    }

    @Override
    public ValidationResult validateProductConstraints(Long productId) {
        logger.debug("Validating product constraints: productId={}", productId);
        ValidationResult result = new ValidationResult();
        
        if (productId == null || productId <= 0) {
            result.addError("productId", "El ID del producto es obligatorio", "INVALID_PRODUCT_ID");
        }
        
        result.setValid(result.getErrors().isEmpty());
        return result;
    }

    @Override
    public ValidationResult createConstraintViolationError(String constraintType, String field, Map<String, Object> details) {
        logger.debug("Creating constraint violation error: type={}, field={}", constraintType, field);
        
        ValidationResult result = new ValidationResult();
        
        String message = "Violación de restricción de base de datos: " + constraintType;
        if (details != null && details.containsKey("message")) {
            message = (String) details.get("message");
        }
        
        result.addError(field, message, constraintType);
        result.setValid(false);
        
        return result;
    }
}
