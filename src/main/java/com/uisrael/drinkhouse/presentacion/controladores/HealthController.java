package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "message", "DrinkHouse API está funcionando correctamente",
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.of(
            "message", "Backend conectado exitosamente",
            "version", "1.0.0",
            "service", "DrinkHouse"
        ));
    }
}

@RestController
class RootController {
    
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        return ResponseEntity.ok(Map.of(
            "service", "DrinkHouse API",
            "status", "UP",
            "version", "1.0.0",
            "message", "API REST para gestión de inventario de bebidas",
            "endpoints", Map.of(
                "health", "/api/v1/health",
                "test", "/api/v1/test",
                "roles", "/api/v1/roles",
                "productos", "/api/v1/productos",
                "proveedores", "/api/v1/proveedores"
            ),
            "timestamp", java.time.LocalDateTime.now().toString()
        ));
    }
}