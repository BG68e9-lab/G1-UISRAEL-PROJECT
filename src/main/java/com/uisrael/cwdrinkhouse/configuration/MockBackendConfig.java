package com.uisrael.cwdrinkhouse.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock backend configuration for development when external backend is not available.
 * Provides mock responses to prevent connection errors.
 */
@Configuration
@ConditionalOnProperty(name = "app.backend.mock.enabled", havingValue = "true", matchIfMissing = false)
public class MockBackendConfig {

    /**
     * Creates a mock WebClient that returns predefined responses instead of making real HTTP calls.
     */
    @Bean
    @Primary
    public WebClient mockWebClient() {
        return WebClient.builder()
                .exchangeFunction(clientRequest -> {
                    // Mock responses based on URL path
                    String path = clientRequest.url().getPath();
                    
                    if (path.contains("/health") || path.contains("/actuator/health")) {
                        return Mono.just(org.springframework.web.reactive.function.client.ClientResponse.create(
                                org.springframework.http.HttpStatus.OK)
                                .header("Content-Type", "application/json")
                                .body("{\"status\":\"UP\"}")
                                .build());
                    }
                    
                    if (path.contains("/api/categories")) {
                        return createMockJsonResponse(getMockCategories());
                    }
                    
                    if (path.contains("/api/products")) {
                        return createMockJsonResponse(getMockProducts());
                    }
                    
                    if (path.contains("/api/providers")) {
                        return createMockJsonResponse(getMockProviders());
                    }
                    
                    if (path.contains("/api/roles")) {
                        return createMockJsonResponse(getMockRoles());
                    }
                    
                    if (path.contains("/api/auth/login")) {
                        return createMockJsonResponse(getMockAuthResponse());
                    }
                    
                    if (path.contains("/api/configurations")) {
                        return createMockJsonResponse(getMockConfigurations());
                    }
                    
                    if (path.contains("/api/v1/ordenes-compra")) {
                        String method = clientRequest.method().name();
                        if ("POST".equals(method)) {
                            return createMockOrderCreationResponse(clientRequest);
                        } else if ("GET".equals(method)) {
                            return createMockJsonResponse(getMockOrders());
                        }
                    }
                    
                    // Default mock response
                    return createMockJsonResponse(Map.of("message", "Mock response", "status", "success"));
                })
                .build();
    }

    private Mono<org.springframework.web.reactive.function.client.ClientResponse> createMockJsonResponse(Object data) {
        return Mono.just(org.springframework.web.reactive.function.client.ClientResponse.create(
                org.springframework.http.HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(convertToJson(data))
                .build());
    }

    private String convertToJson(Object data) {
        try {
            // Use Jackson ObjectMapper for proper JSON serialization
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(data);
        } catch (Exception e) {
            // Fallback to simple string conversion
            if (data instanceof String) {
                return (String) data;
            }
            return "{\"data\": \"" + data.toString() + "\"}";
        }
    }

    private List<Map<String, Object>> getMockCategories() {
        return List.of(
            Map.of("id", 1, "nombre", "Bebidas Alcohólicas", "descripcion", "Categoría de bebidas con alcohol", "estado", "ACTIVO"),
            Map.of("id", 2, "nombre", "Bebidas No Alcohólicas", "descripcion", "Categoría de bebidas sin alcohol", "estado", "ACTIVO"),
            Map.of("id", 3, "nombre", "Snacks", "descripcion", "Aperitivos y comida rápida", "estado", "ACTIVO")
        );
    }

    private List<Map<String, Object>> getMockProducts() {
        return List.of(
            Map.of("id", 1, "nombre", "Cerveza Corona", "descripcion", "Cerveza premium", "precio", 2.50, "stock", 100, "categoriaId", 1),
            Map.of("id", 2, "nombre", "Coca Cola", "descripcion", "Refresco de cola", "precio", 1.25, "stock", 200, "categoriaId", 2),
            Map.of("id", 3, "nombre", "Papas Fritas", "descripcion", "Snack salado", "precio", 0.75, "stock", 50, "categoriaId", 3)
        );
    }

    private List<Map<String, Object>> getMockProviders() {
        return List.of(
            Map.of("id", 1, "nombre", "Distribuidora Central", "ruc", "1234567890", "email", "central@example.com", "telefono", "0987654321"),
            Map.of("id", 2, "nombre", "Bebidas del Pacífico", "ruc", "0987654321", "email", "pacifico@example.com", "telefono", "1234567890")
        );
    }

    private List<Map<String, Object>> getMockRoles() {
        return List.of(
            Map.of("id", 1, "nombre", "ADMINISTRADOR", "descripcion", "Acceso total al sistema"),
            Map.of("id", 2, "nombre", "VENDEDOR", "descripcion", "Acceso a ventas y consultas"),
            Map.of("id", 3, "nombre", "INVENTARIO", "descripcion", "Gestión de inventario")
        );
    }

    private Map<String, Object> getMockAuthResponse() {
        return Map.of(
            "token", "mock-jwt-token-12345",
            "user", Map.of(
                "id", 1,
                "email", "admin@drinkhouse.com",
                "nombre", "Administrador",
                "rol", "ADMINISTRADOR"
            ),
            "expiresIn", 3600
        );
    }

    private List<Map<String, Object>> getMockConfigurations() {
        return List.of(
            Map.of("key", "app.name", "value", "DrinkHouse", "description", "Nombre de la aplicación"),
            Map.of("key", "cache.enabled", "value", "true", "description", "Caché habilitado"),
            Map.of("key", "session.timeout", "value", "30", "description", "Timeout de sesión en minutos")
        );
    }
    
    private Mono<org.springframework.web.reactive.function.client.ClientResponse> createMockOrderCreationResponse(
            org.springframework.web.reactive.function.client.ClientRequest clientRequest) {
        
        // Extract order data from request body (simplified)
        long mockOrderId = System.currentTimeMillis() % 1000000; // Simple ID generation
        String mockReferenceCode = "ORD-" + java.time.LocalDate.now().toString().replace("-", "") + "-" + (mockOrderId % 1000);
        
        Map<String, Object> mockOrder = Map.of(
            "ordenCompraId", mockOrderId,
            "codigoReferencia", mockReferenceCode,
            "proveedorId", 1,
            "estado", "BORRADOR",
            "total", 25.75,
            "fechaCreacion", java.time.LocalDateTime.now().toString(),
            "negocioId", 1,
            "detalles", List.of(
                Map.of("detalleId", 1, "productoId", 1, "cantidad", 10, "precioUnitario", 2.50, "subtotal", 25.00),
                Map.of("detalleId", 2, "productoId", 2, "cantidad", 1, "precioUnitario", 0.75, "subtotal", 0.75)
            )
        );
        
        return createMockJsonResponse(mockOrder);
    }
    
    private List<Map<String, Object>> getMockOrders() {
        return List.of(
            Map.of(
                "ordenCompraId", 1,
                "codigoReferencia", "ORD-20240101-001",
                "proveedorId", 1,
                "estado", "BORRADOR",
                "total", 125.50,
                "fechaCreacion", java.time.LocalDateTime.now().minusDays(1).toString()
            ),
            Map.of(
                "ordenCompraId", 2,
                "codigoReferencia", "ORD-20240101-002",
                "proveedorId", 1,
                "estado", "ENVIADA",
                "total", 75.25,
                "fechaCreacion", java.time.LocalDateTime.now().minusHours(12).toString()
            )
        );
    }
}