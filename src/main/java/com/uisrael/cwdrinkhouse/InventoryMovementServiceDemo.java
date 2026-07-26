package com.uisrael.cwdrinkhouse;

import com.uisrael.cwdrinkhouse.dto.InventoryMovementDTO;
import com.uisrael.cwdrinkhouse.service.impl.InventoryMovementServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Demo class to validate InventoryMovementService functionality.
 * Demonstrates movement code generation and validation logic.
 */
public class InventoryMovementServiceDemo {
    
    public static void main(String[] args) {
        // Create service instance (with null dependencies for demo)
        InventoryMovementServiceImpl service = new InventoryMovementServiceImpl(null, null, null);
        
        System.out.println("=== Inventory Movement Service Demo ===");
        
        // Test movement code generation
        System.out.println("\n1. Movement Code Generation:");
        System.out.println("ENTRADA: " + service.generateMovementCode("ENTRADA"));
        System.out.println("SALIDA: " + service.generateMovementCode("SALIDA"));
        System.out.println("AJUSTE: " + service.generateMovementCode("AJUSTE"));
        
        // Test valid ENTRADA movement
        System.out.println("\n2. Valid ENTRADA Movement:");
        InventoryMovementDTO entradaMovement = createValidEntradaMovement();
        System.out.println("Valid: " + entradaMovement.isValidMovement());
        System.out.println("Error: " + entradaMovement.getValidationError());
        
        // Test invalid ENTRADA movement (missing precio)
        System.out.println("\n3. Invalid ENTRADA Movement (missing precio):");
        InventoryMovementDTO invalidEntrada = createInvalidEntradaMovement();
        System.out.println("Valid: " + invalidEntrada.isValidMovement());
        System.out.println("Error: " + invalidEntrada.getValidationError());
        
        // Test valid SALIDA movement
        System.out.println("\n4. Valid SALIDA Movement:");
        InventoryMovementDTO salidaMovement = createValidSalidaMovement();
        System.out.println("Valid: " + salidaMovement.isValidMovement());
        System.out.println("Error: " + salidaMovement.getValidationError());
        
        // Test invalid SALIDA movement (missing lote)
        System.out.println("\n5. Invalid SALIDA Movement (missing lote):");
        InventoryMovementDTO invalidSalida = createInvalidSalidaMovement();
        System.out.println("Valid: " + invalidSalida.isValidMovement());
        System.out.println("Error: " + invalidSalida.getValidationError());
        
        // Test valid AJUSTE movement
        System.out.println("\n6. Valid AJUSTE Movement:");
        InventoryMovementDTO ajusteMovement = createValidAjusteMovement();
        System.out.println("Valid: " + ajusteMovement.isValidMovement());
        System.out.println("Error: " + ajusteMovement.getValidationError());
        
        // Test invalid AJUSTE movement (zero quantity)
        System.out.println("\n7. Invalid AJUSTE Movement (zero quantity):");
        InventoryMovementDTO invalidAjuste = createInvalidAjusteMovement();
        System.out.println("Valid: " + invalidAjuste.isValidMovement());
        System.out.println("Error: " + invalidAjuste.getValidationError());
        
        // Test stock validation logic
        System.out.println("\n8. Stock Validation Logic:");
        System.out.println("Sufficient stock (request=5, available=10): " + 
                          service.validateStockAvailability(1L, 1L, 5));
        System.out.println("Insufficient stock (request=15, available=10): " + 
                          service.validateStockAvailability(1L, 1L, 15));
        System.out.println("Invalid parameters (null product): " + 
                          service.validateStockAvailability(null, 1L, 5));
        
        System.out.println("\n=== Demo completed successfully ===");
    }
    
    private static InventoryMovementDTO createValidEntradaMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("ENTRADA");
        dto.setProductoId(1L);
        dto.setLoteId(1L);
        dto.setCantidad(10);
        dto.setPrecioUnitario(new BigDecimal("15.50"));
        dto.setDescripcion("Entrada de prueba");
        dto.setFechaMovimiento(LocalDateTime.now());
        return dto;
    }
    
    private static InventoryMovementDTO createInvalidEntradaMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("ENTRADA");
        dto.setProductoId(1L);
        dto.setLoteId(1L);
        dto.setCantidad(10);
        // Missing precioUnitario - should be invalid
        dto.setDescripcion("Entrada inválida");
        return dto;
    }
    
    private static InventoryMovementDTO createValidSalidaMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("SALIDA");
        dto.setProductoId(1L);
        dto.setLoteId(1L);
        dto.setCantidad(5);
        dto.setDescripcion("Salida de prueba");
        dto.setFechaMovimiento(LocalDateTime.now());
        return dto;
    }
    
    private static InventoryMovementDTO createInvalidSalidaMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("SALIDA");
        dto.setProductoId(1L);
        dto.setCantidad(5);
        // Missing loteId - should be invalid
        dto.setDescripcion("Salida inválida");
        return dto;
    }
    
    private static InventoryMovementDTO createValidAjusteMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("AJUSTE");
        dto.setProductoId(1L);
        dto.setCantidad(-3); // Negative adjustment
        dto.setDescripcion("Ajuste de inventario");
        dto.setFechaMovimiento(LocalDateTime.now());
        return dto;
    }
    
    private static InventoryMovementDTO createInvalidAjusteMovement() {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        dto.setTipo("AJUSTE");
        dto.setProductoId(1L);
        dto.setCantidad(0); // Zero quantity - should be invalid for AJUSTE
        dto.setDescripcion("Ajuste inválido");
        return dto;
    }
}