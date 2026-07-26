package com.uisrael.cwdrinkhouse.dto;

import com.uisrael.cwdrinkhouse.service.OrderValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Unit tests for OrderDTO enhanced validation capabilities.
 * Tests the new validation methods that integrate with OrderValidationService.
 * 
 * Requirements: 2.2, 5.5
 */
@DisplayName("OrderDTO Enhanced Validation Tests")
class OrderDTOEnhancedValidationTest {

    @Mock
    private OrderValidationService mockValidationService;

    private OrderDTO orderDTO;
    private OrderDetailDTO detailDTO1;
    private OrderDetailDTO detailDTO2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a sample order with details
        orderDTO = new OrderDTO();
        orderDTO.setOrdenCompraId(1L);
        orderDTO.setCodigoReferencia("ORD-20260726-143000");
        orderDTO.setProveedorId(100L);
        orderDTO.setProveedorRazonSocial("Test Provider S.A.");
        orderDTO.setNegocioId(1);
        orderDTO.setEstado("BORRADOR");
        orderDTO.setTotal(new BigDecimal("150.50"));
        orderDTO.setFechaCreacion(LocalDateTime.now());
        orderDTO.setUsuarioCreacion("testuser");
        orderDTO.setObservaciones("Test order for validation");

        // Create order details
        detailDTO1 = new OrderDetailDTO();
        detailDTO1.setDetalleId(1L);
        detailDTO1.setProductoId(200L);
        detailDTO1.setProductoNombre("Test Product 1");
        detailDTO1.setProductoMarca("Test Brand");
        detailDTO1.setProductoTipo("Bebida");
        detailDTO1.setCantidad(3);
        detailDTO1.setPrecioUnitario(new BigDecimal("25.50"));
        detailDTO1.calculateSubtotal();

        detailDTO2 = new OrderDetailDTO();
        detailDTO2.setDetalleId(2L);
        detailDTO2.setProductoId(201L);
        detailDTO2.setProductoNombre("Test Product 2");
        detailDTO2.setProductoMarca("Test Brand 2");
        detailDTO2.setProductoTipo("Snack");
        detailDTO2.setCantidad(2);
        detailDTO2.setPrecioUnitario(new BigDecimal("37.75"));
        detailDTO2.calculateSubtotal();

        List<OrderDetailDTO> details = new ArrayList<>();
        details.add(detailDTO1);
        details.add(detailDTO2);
        orderDTO.setDetalles(details);
        orderDTO.calculateTotal();
    }

    @Test
    @DisplayName("Enhanced validateForCreation should detect invalid provider ID")
    void testEnhancedValidateForCreation_InvalidProviderID() {
        // Given
        orderDTO.setProveedorId(null);
        
        // When
        List<ValidationResult.ValidationError> errors = orderDTO.validateForCreation();
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> 
            "proveedorId".equals(error.getFieldName()) && 
            "REQUIRED_FIELD".equals(error.getErrorCode())));
    }

    @Test
    @DisplayName("Enhanced validateForCreation should detect negative provider ID")
    void testEnhancedValidateForCreation_NegativeProviderID() {
        // Given
        orderDTO.setProveedorId(-1L);
        
        // When
        List<ValidationResult.ValidationError> errors = orderDTO.validateForCreation();
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> 
            "proveedorId".equals(error.getFieldName()) && 
            "INVALID_VALUE".equals(error.getErrorCode())));
    }

    @Test
    @DisplayName("Enhanced validateForCreation should detect invalid state")
    void testEnhancedValidateForCreation_InvalidState() {
        // Given
        orderDTO.setEstado("INVALID_STATE");
        
        // When
        List<ValidationResult.ValidationError> errors = orderDTO.validateForCreation();
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> 
            "estado".equals(error.getFieldName()) && 
            "INVALID_STATE".equals(error.getErrorCode())));
    }

    @Test
    @DisplayName("validateForCreationWithService should use OrderValidationService")
    void testValidateForCreationWithService() {
        // Given
        ValidationResult expectedResult = new ValidationResult(true);
        when(mockValidationService.validateOrderData(orderDTO)).thenReturn(expectedResult);
        
        // When
        ValidationResult result = orderDTO.validateForCreationWithService(mockValidationService);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isValid());
    }

    @Test
    @DisplayName("validateForCreationWithService should throw exception with null service")
    void testValidateForCreationWithService_NullService() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> orderDTO.validateForCreationWithService(null));
        
        assertEquals("OrderValidationService is required for comprehensive validation", 
            exception.getMessage());
    }

    @Test
    @DisplayName("hasValidProviderReferenceWithService should use OrderValidationService")
    void testHasValidProviderReferenceWithService() {
        // Given
        ValidationResult validResult = new ValidationResult(true);
        when(mockValidationService.validateProviderExists(orderDTO.getProveedorId())).thenReturn(validResult);
        
        // When
        boolean result = orderDTO.hasValidProviderReferenceWithService(mockValidationService);
        
        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("hasValidProductReferencesWithService should use OrderValidationService")
    void testHasValidProductReferencesWithService() {
        // Given
        ValidationResult validResult = new ValidationResult(true);
        when(mockValidationService.validateProductsExist(anyList())).thenReturn(validResult);
        
        // When
        boolean result = orderDTO.hasValidProductReferencesWithService(mockValidationService);
        
        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("generateDiagnosticSummary should include comprehensive information")
    void testGenerateDiagnosticSummary() {
        // When
        String summary = orderDTO.generateDiagnosticSummary();
        
        // Then
        assertNotNull(summary);
        assertFalse(summary.trim().isEmpty());
        assertTrue(summary.contains("OrderDTO Diagnostic Summary"));
        assertTrue(summary.contains("Order ID: 1"));
        assertTrue(summary.contains("Provider ID: 100"));
        assertTrue(summary.contains("Status: BORRADOR"));
        assertTrue(summary.contains("Financial Information"));
        assertTrue(summary.contains("Order Details Analysis"));
        assertTrue(summary.contains("Validation Summary"));
        assertTrue(summary.contains("Business Rules Check"));
        assertTrue(summary.contains("Details Count: 2"));
    }

    @Test
    @DisplayName("extractValidationContext should provide structured validation context")
    void testExtractValidationContext() {
        // When
        Map<String, Object> context = orderDTO.extractValidationContext();
        
        // Then
        assertNotNull(context);
        assertFalse(context.isEmpty());
        
        // Verify basic information
        assertEquals(1L, context.get("ordenCompraId"));
        assertEquals(100L, context.get("proveedorId"));
        assertEquals("BORRADOR", context.get("estado"));
        
        // Verify financial context
        @SuppressWarnings("unchecked")
        Map<String, Object> financial = (Map<String, Object>) context.get("financial");
        assertNotNull(financial);
        assertTrue(financial.containsKey("storedTotal"));
        assertTrue(financial.containsKey("calculatedTotal"));
        assertTrue(financial.containsKey("totalMatch"));
        
        // Verify details context
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) context.get("details");
        assertNotNull(details);
        assertEquals(2, details.get("count"));
        assertEquals(Boolean.TRUE, details.get("hasDetails"));
        
        // Verify validation summary
        @SuppressWarnings("unchecked")
        Map<String, Object> validation = (Map<String, Object>) context.get("validation");
        assertNotNull(validation);
        assertTrue(validation.containsKey("hasValidProviderRef"));
        assertTrue(validation.containsKey("hasValidProductRefs"));
        assertTrue(validation.containsKey("totalCalculationOK"));
        assertTrue(validation.containsKey("isEditable"));
        
        // Verify business context
        @SuppressWarnings("unchecked")
        Map<String, Object> business = (Map<String, Object>) context.get("business");
        assertNotNull(business);
        assertEquals("BORRADOR", business.get("currentState"));
        assertEquals(Boolean.TRUE, business.get("isValidState"));
        
        // Verify technical context
        @SuppressWarnings("unchecked")
        Map<String, Object> technical = (Map<String, Object>) context.get("technical");
        assertNotNull(technical);
        assertTrue(technical.containsKey("objectHashCode"));
        assertTrue(technical.containsKey("className"));
    }

    @Test
    @DisplayName("extractValidationContext should include details analysis")
    void testExtractValidationContext_DetailsAnalysis() {
        // When
        Map<String, Object> context = orderDTO.extractValidationContext();
        
        // Then
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) context.get("details");
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) details.get("items");
        assertNotNull(items);
        assertEquals(2, items.size());
        
        // Check first detail
        Map<String, Object> firstDetail = items.get(0);
        assertEquals(0, firstDetail.get("index"));
        assertEquals(200L, firstDetail.get("productoId"));
        assertEquals(3, firstDetail.get("cantidad"));
        assertEquals(new BigDecimal("25.50"), firstDetail.get("precioUnitario"));
        
        @SuppressWarnings("unchecked")
        Map<String, Boolean> firstValidation = (Map<String, Boolean>) firstDetail.get("validation");
        assertEquals(Boolean.TRUE, firstValidation.get("hasValidProductId"));
        assertEquals(Boolean.TRUE, firstValidation.get("hasValidQuantity"));
        assertEquals(Boolean.TRUE, firstValidation.get("hasValidPrice"));
    }

    @Test
    @DisplayName("Basic validation should work without OrderValidationService")
    void testBasicValidationWithoutService() {
        // When
        boolean hasValidProvider = orderDTO.hasValidProviderReference();
        boolean hasValidProducts = orderDTO.hasValidProductReferences();
        boolean hasValidTotal = orderDTO.hasProfessionalTotalCalculation();
        
        // Then
        assertTrue(hasValidProvider);
        assertTrue(hasValidProducts);
        assertTrue(hasValidTotal);
    }

    @Test
    @DisplayName("Validation should detect empty order details")
    void testValidationWithEmptyDetails() {
        // Given
        orderDTO.setDetalles(new ArrayList<>());
        orderDTO.setTotal(BigDecimal.ZERO);
        
        // When
        List<ValidationResult.ValidationError> errors = orderDTO.validateForCreation();
        boolean hasValidProducts = orderDTO.hasValidProductReferences();
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> 
            "detalles".equals(error.getFieldName()) && 
            "REQUIRED_FIELD".equals(error.getErrorCode())));
        assertFalse(hasValidProducts);
    }

    @Test
    @DisplayName("Validation should detect total calculation mismatch")
    void testValidationWithTotalMismatch() {
        // Given
        orderDTO.setTotal(new BigDecimal("999.99")); // Wrong total
        
        // When
        List<ValidationResult.ValidationError> errors = orderDTO.validateForCreation();
        boolean hasValidTotal = orderDTO.hasProfessionalTotalCalculation();
        
        // Then
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(error -> 
            "total".equals(error.getFieldName()) && 
            "CALCULATION_MISMATCH".equals(error.getErrorCode())));
        assertFalse(hasValidTotal);
    }
}