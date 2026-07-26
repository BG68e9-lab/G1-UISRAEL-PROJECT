package com.uisrael.cwdrinkhouse.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Demonstration program to showcase OrderDTO enhanced validation capabilities.
 * This program shows the new validation methods and diagnostic features.
 */
public class OrderDTOValidationDemo {

    public static void main(String[] args) {
        System.out.println("=== OrderDTO Enhanced Validation Demonstration ===\n");
        
        // Create a sample order with valid data
        OrderDTO validOrder = createValidOrder();
        demonstrateValidOrder(validOrder);
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Create a sample order with invalid data
        OrderDTO invalidOrder = createInvalidOrder();
        demonstrateInvalidOrder(invalidOrder);
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Demonstrate diagnostic capabilities
        demonstrateDiagnosticCapabilities(validOrder);
    }

    private static OrderDTO createValidOrder() {
        OrderDTO order = new OrderDTO();
        order.setOrdenCompraId(1L);
        order.setCodigoReferencia("ORD-20260726-143000");
        order.setProveedorId(100L);
        order.setProveedorRazonSocial("Distribuidora ABC S.A.");
        order.setNegocioId(1);
        order.setEstado("BORRADOR");
        order.setFechaCreacion(LocalDateTime.now());
        order.setUsuarioCreacion("admin");
        order.setObservaciones("Orden de prueba para demostración");

        // Add valid order details
        List<OrderDetailDTO> details = new ArrayList<>();
        
        OrderDetailDTO detail1 = new OrderDetailDTO();
        detail1.setDetalleId(1L);
        detail1.setProductoId(200L);
        detail1.setProductoNombre("Coca Cola 600ml");
        detail1.setProductoMarca("Coca Cola");
        detail1.setProductoTipo("Bebida");
        detail1.setCantidad(10);
        detail1.setPrecioUnitario(new BigDecimal("2.50"));
        detail1.calculateSubtotal();
        details.add(detail1);

        OrderDetailDTO detail2 = new OrderDetailDTO();
        detail2.setDetalleId(2L);
        detail2.setProductoId(201L);
        detail2.setProductoNombre("Papas Fritas");
        detail2.setProductoMarca("Lays");
        detail2.setProductoTipo("Snack");
        detail2.setCantidad(5);
        detail2.setPrecioUnitario(new BigDecimal("1.75"));
        detail2.calculateSubtotal();
        details.add(detail2);

        order.setDetalles(details);
        order.calculateTotal();
        
        return order;
    }

    private static OrderDTO createInvalidOrder() {
        OrderDTO order = new OrderDTO();
        order.setOrdenCompraId(2L);
        order.setCodigoReferencia("INVALID-CODE");
        order.setProveedorId(null); // Invalid: null provider
        order.setEstado("INVALID_STATE"); // Invalid: bad state
        order.setTotal(new BigDecimal("999.99")); // Invalid: will not match calculated total
        order.setFechaCreacion(LocalDateTime.now());
        order.setUsuarioCreacion("testuser");

        // Add invalid order details
        List<OrderDetailDTO> details = new ArrayList<>();
        
        OrderDetailDTO invalidDetail = new OrderDetailDTO();
        invalidDetail.setDetalleId(1L);
        invalidDetail.setProductoId(-1L); // Invalid: negative product ID
        invalidDetail.setCantidad(-5); // Invalid: negative quantity
        invalidDetail.setPrecioUnitario(new BigDecimal("-10.00")); // Invalid: negative price
        details.add(invalidDetail);

        order.setDetalles(details);
        // Don't recalculate total to create mismatch
        
        return order;
    }

    private static void demonstrateValidOrder(OrderDTO order) {
        System.out.println("DEMONSTRATING VALID ORDER:");
        System.out.println("Order ID: " + order.getOrdenCompraId());
        System.out.println("Provider ID: " + order.getProveedorId());
        System.out.println("State: " + order.getEstado());
        System.out.println("Details Count: " + (order.getDetalles() != null ? order.getDetalles().size() : 0));
        
        // Test basic validation methods
        System.out.println("\n--- Basic Validation Results ---");
        System.out.println("Has valid provider reference: " + order.hasValidProviderReference());
        System.out.println("Has valid product references: " + order.hasValidProductReferences());
        System.out.println("Has professional total calculation: " + order.hasProfessionalTotalCalculation());
        System.out.println("Is editable: " + order.isEditable());
        
        // Test validation for creation
        List<ValidationResult.ValidationError> errors = order.validateForCreation();
        System.out.println("\n--- Validation Errors ---");
        System.out.println("Total errors: " + errors.size());
        if (errors.isEmpty()) {
            System.out.println("✓ Order passes all local validations!");
        } else {
            errors.forEach(error -> 
                System.out.println("✗ " + error.getFieldName() + ": " + error.getMessage()));
        }
    }

    private static void demonstrateInvalidOrder(OrderDTO order) {
        System.out.println("DEMONSTRATING INVALID ORDER:");
        System.out.println("Order ID: " + order.getOrdenCompraId());
        System.out.println("Provider ID: " + order.getProveedorId());
        System.out.println("State: " + order.getEstado());
        System.out.println("Stored Total: " + order.getTotal());
        System.out.println("Calculated Total: " + order.calculateTotal());
        
        // Test basic validation methods
        System.out.println("\n--- Basic Validation Results ---");
        System.out.println("Has valid provider reference: " + order.hasValidProviderReference());
        System.out.println("Has valid product references: " + order.hasValidProductReferences());
        System.out.println("Has professional total calculation: " + order.hasProfessionalTotalCalculation());
        System.out.println("Is editable: " + order.isEditable());
        
        // Test validation for creation
        List<ValidationResult.ValidationError> errors = order.validateForCreation();
        System.out.println("\n--- Validation Errors ---");
        System.out.println("Total errors: " + errors.size());
        if (!errors.isEmpty()) {
            System.out.println("✗ Order has validation errors:");
            errors.forEach(error -> 
                System.out.println("  - " + error.getFieldName() + ": " + error.getMessage() + " (Code: " + error.getErrorCode() + ")"));
        }
    }

    private static void demonstrateDiagnosticCapabilities(OrderDTO order) {
        System.out.println("DEMONSTRATING DIAGNOSTIC CAPABILITIES:");
        
        // Generate diagnostic summary
        System.out.println("\n--- Diagnostic Summary ---");
        String summary = order.generateDiagnosticSummary();
        System.out.println(summary);
        
        // Extract validation context
        System.out.println("\n--- Validation Context (Key Information) ---");
        Map<String, Object> context = order.extractValidationContext();
        
        System.out.println("Extraction timestamp: " + context.get("extractionTimestamp"));
        System.out.println("Order ID: " + context.get("ordenCompraId"));
        System.out.println("Provider: " + context.get("proveedorRazonSocial"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> financial = (Map<String, Object>) context.get("financial");
        if (financial != null) {
            System.out.println("Stored Total: " + financial.get("storedTotal"));
            System.out.println("Calculated Total: " + financial.get("calculatedTotal"));
            System.out.println("Total Match: " + financial.get("totalMatch"));
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> validation = (Map<String, Object>) context.get("validation");
        if (validation != null) {
            System.out.println("Local Validation Errors: " + validation.get("localValidationErrorCount"));
            System.out.println("Is Locally Valid: " + validation.get("isLocallyValid"));
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> business = (Map<String, Object>) context.get("business");
        if (business != null) {
            System.out.println("Current State: " + business.get("currentState"));
            System.out.println("Is Valid State: " + business.get("isValidState"));
            @SuppressWarnings("unchecked")
            List<String> transitions = (List<String>) business.get("availableTransitions");
            System.out.println("Available Transitions: " + transitions);
        }
        
        System.out.println("\n✓ Diagnostic capabilities successfully demonstrated!");
    }
}