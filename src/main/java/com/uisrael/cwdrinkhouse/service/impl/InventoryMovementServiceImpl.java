package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.InventoryMovementDTO;
import com.uisrael.cwdrinkhouse.service.InventoryMovementService;
import com.uisrael.cwdrinkhouse.service.CacheManager;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.exception.ExternalServiceException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of InventoryMovementService providing complete inventory movement management.
 * Handles ENTRADA, SALIDA, and AJUSTE movement types with type-specific validation.
 * 
 * Features:
 * - Paginated movement listing with type and date filtering
 * - Type-specific validation (ENTRADA, SALIDA, AJUSTE)
 * - Stock availability validation for SALIDA movements
 * - Movement code generation with type-specific prefixes
 * - Comprehensive error handling and logging
 * - Cache management with automatic invalidation
 * - Retry logic for transient failures
 * 
 * Requirements: 7.1-7.12, 18.5-18.6
 */
@Service
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryMovementServiceImpl.class);

    private final WebClient webClient;
    private final Retry retryConfiguration;
    private final CacheManager cacheManager;

    // Movement type prefixes for code generation
    private static final Map<String, String> TYPE_PREFIXES = Map.of(
        "ENTRADA", "ENT",
        "SALIDA", "SAL", 
        "AJUSTE", "AJU"
    );
    // Cache configuration
    private static final String MOVEMENTS_CACHE_KEY = "movements:page:%d:size:%d";
    private static final String MOVEMENTS_TYPE_CACHE_KEY = "movements:type:%s:page:%d:size:%d";
    private static final String MOVEMENTS_DATE_CACHE_KEY = "movements:date:%s:%s:page:%d:size:%d";
    private static final String MOVEMENTS_FILTER_CACHE_KEY = "movements:filter:%s:%s:%s:page:%d:size:%d";
    private static final String MOVEMENT_CACHE_KEY = "movement:%d";
    private static final String MOVEMENTS_CACHE_PATTERN = "movements:*";
    private static final String MOVEMENT_PATTERN = "movement:*";
    private static final String STOCK_CACHE_KEY = "stock:%d:%d";
    private static final String STOCK_CACHE_PATTERN = "stock:*";

    @Autowired
    public InventoryMovementServiceImpl(WebClient webClient, 
                                      Retry webClientRetry, 
                                      CacheManager cacheManager) {
        this.webClient = webClient;
        this.retryConfiguration = webClientRetry;
        this.cacheManager = cacheManager;
    }

    @Override
    public Page<InventoryMovementDTO> getAllMovements(int page, int size) {
        logger.debug("Retrieving inventory movements page {} with size {}", page, size);

        // Check cache first
        String cacheKey = String.format(MOVEMENTS_CACHE_KEY, page, size);
        Page<InventoryMovementDTO> cachedResult = cacheManager.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Returning cached movements for page {} size {}", page, size);
            return cachedResult;
        }

        try {
            String uri = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            Page<InventoryMovementDTO> result = mapResponseToPage(response, page, size);
            
            // Cache the result
            cacheManager.put(cacheKey, result, 300); // 5 minutes TTL for movements
            
            return result;

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements: HTTP {} - {}", 
                        e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos de inventario");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos", e);
        }
    }
    @Override
    public Page<InventoryMovementDTO> getMovementsByType(String tipo, int page, int size) {
        logger.debug("Retrieving movements by type '{}' page {} with size {}", tipo, page, size);

        if (!StringUtils.hasText(tipo)) {
            throw new ValidationException("El tipo de movimiento es obligatorio");
        }

        if (!isValidMovementType(tipo)) {
            throw new ValidationException("Tipo de movimiento inválido. Debe ser ENTRADA, SALIDA o AJUSTE");
        }

        String cacheKey = String.format(MOVEMENTS_TYPE_CACHE_KEY, tipo, page, size);
        Page<InventoryMovementDTO> cachedResult = cacheManager.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Returning cached movements for type {} page {} size {}", tipo, page, size);
            return cachedResult;
        }

        try {
            String uri = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("tipo", tipo)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            Page<InventoryMovementDTO> result = mapResponseToPage(response, page, size);
            
            // Cache the result
            cacheManager.put(cacheKey, result, 300); // 5 minutes TTL
            
            return result;

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements by type '{}': HTTP {} - {}", 
                        tipo, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos por tipo");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements by type '{}': {}", tipo, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos por tipo", e);
        }
    }

    @Override
    public Page<InventoryMovementDTO> getMovementsByDateRange(LocalDateTime fechaInicio, 
                                                            LocalDateTime fechaFin, 
                                                            int page, int size) {
        logger.debug("Retrieving movements by date range {} to {} page {} with size {}", 
                    fechaInicio, fechaFin, page, size);

        if (fechaInicio == null || fechaFin == null) {
            throw new ValidationException("Las fechas de inicio y fin son obligatorias");
        }

        if (fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        String fechaInicioStr = fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String fechaFinStr = fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        String cacheKey = String.format(MOVEMENTS_DATE_CACHE_KEY, fechaInicioStr, fechaFinStr, page, size);
        Page<InventoryMovementDTO> cachedResult = cacheManager.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Returning cached movements for date range {} to {}", fechaInicio, fechaFin);
            return cachedResult;
        }
        try {
            String uri = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("fechaInicio", fechaInicioStr)
                    .queryParam("fechaFin", fechaFinStr)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            Page<InventoryMovementDTO> result = mapResponseToPage(response, page, size);
            
            // Cache the result
            cacheManager.put(cacheKey, result, 300); // 5 minutes TTL
            
            return result;

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements by date range: HTTP {} - {}", 
                        e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos por rango de fechas");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements by date range: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos por fecha", e);
        }
    }

    @Override
    public Page<InventoryMovementDTO> getMovementsWithFilters(String tipo, 
                                                            LocalDateTime fechaInicio, 
                                                            LocalDateTime fechaFin, 
                                                            int page, int size) {
        logger.debug("Retrieving movements with filters - tipo: {}, fechaInicio: {}, fechaFin: {}, page: {}, size: {}", 
                    tipo, fechaInicio, fechaFin, page, size);

        // Validate type if provided
        if (StringUtils.hasText(tipo) && !isValidMovementType(tipo)) {
            throw new ValidationException("Tipo de movimiento inválido. Debe ser ENTRADA, SALIDA o AJUSTE");
        }

        // Validate date range if both provided
        if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
            throw new ValidationException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        String fechaInicioStr = fechaInicio != null ? fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
        String fechaFinStr = fechaFin != null ? fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "";
        String tipoStr = tipo != null ? tipo : "";
        
        String cacheKey = String.format(MOVEMENTS_FILTER_CACHE_KEY, tipoStr, fechaInicioStr, fechaFinStr, page, size);
        Page<InventoryMovementDTO> cachedResult = cacheManager.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("Returning cached movements with filters");
            return cachedResult;
        }
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("page", page)
                    .queryParam("size", size);

            if (StringUtils.hasText(tipo)) {
                uriBuilder.queryParam("tipo", tipo);
            }
            if (fechaInicio != null) {
                uriBuilder.queryParam("fechaInicio", fechaInicioStr);
            }
            if (fechaFin != null) {
                uriBuilder.queryParam("fechaFin", fechaFinStr);
            }

            String uri = uriBuilder.build().toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            Page<InventoryMovementDTO> result = mapResponseToPage(response, page, size);
            
            // Cache the result
            cacheManager.put(cacheKey, result, 300); // 5 minutes TTL
            
            return result;

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements with filters: HTTP {} - {}", 
                        e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos con filtros");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements with filters: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos con filtros", e);
        }
    }

    @Override
    public InventoryMovementDTO getMovementById(Long movimientoId) {
        logger.debug("Retrieving movement with ID: {}", movimientoId);

        if (movimientoId == null) {
            throw new ValidationException("El ID del movimiento es obligatorio");
        }

        // Check cache first
        String cacheKey = String.format(MOVEMENT_CACHE_KEY, movimientoId);
        InventoryMovementDTO cachedMovement = cacheManager.get(cacheKey);
        if (cachedMovement != null) {
            logger.debug("Returning cached movement with ID: {}", movimientoId);
            return cachedMovement;
        }

        try {
            InventoryMovementDTO movement = webClient.get()
                    .uri("/movimientos-inventario/{id}", movimientoId)
                    .retrieve()
                    .bodyToMono(InventoryMovementDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (movement != null) {
                // Cache the movement
                cacheManager.put(cacheKey, movement, 300); // 5 minutes TTL
                logger.debug("Retrieved and cached movement: {}", movement.getCodigoMovimiento());
            }

            return movement;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Movement with ID {} not found", movimientoId);
                throw new EntityNotFoundException("Movimiento con ID " + movimientoId + " no encontrado");
            }
            
            logger.error("Error retrieving movement {}: HTTP {} - {}", 
                        movimientoId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimiento");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movement {}: {}", movimientoId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimiento", e);
        }
    }
    @Override
    public InventoryMovementDTO createMovement(InventoryMovementDTO movementDTO) {
        logger.debug("Creating new inventory movement: {} - {}", 
                    movementDTO.getTipo(), movementDTO.getProductoId());

        if (movementDTO == null) {
            throw new ValidationException("Los datos del movimiento son obligatorios");
        }

        // Validate movement type
        if (!StringUtils.hasText(movementDTO.getTipo())) {
            throw new ValidationException("El tipo de movimiento es obligatorio");
        }

        if (!isValidMovementType(movementDTO.getTipo())) {
            throw new ValidationException("Tipo de movimiento inválido. Debe ser ENTRADA, SALIDA o AJUSTE");
        }

        // Type-specific validation
        validateMovementByType(movementDTO);

        // For SALIDA movements, validate stock availability
        if ("SALIDA".equals(movementDTO.getTipo())) {
            if (!validateStockAvailability(movementDTO.getProductoId(), 
                                         movementDTO.getLoteId(), 
                                         movementDTO.getCantidad())) {
                throw new BusinessRuleException("Stock insuficiente para realizar la salida. " +
                    "Cantidad disponible: " + getAvailableStock(movementDTO.getProductoId(), movementDTO.getLoteId()) +
                    ", cantidad solicitada: " + movementDTO.getCantidad());
            }
        }

        try {
            InventoryMovementDTO createdMovement = webClient.post()
                    .uri("/movimientos-inventario")
                    .bodyValue(movementDTO)
                    .retrieve()
                    .bodyToMono(InventoryMovementDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdMovement != null) {
                // Invalidate movements and stock cache since new movement was added
                invalidateMovementsCache();
                invalidateStockCache();
                
                // Cache the new movement
                String cacheKey = String.format(MOVEMENT_CACHE_KEY, createdMovement.getMovimientoId());
                cacheManager.put(cacheKey, createdMovement, 300);
                
                logger.info("Created new movement: {} with ID: {}", 
                           createdMovement.getCodigoMovimiento(), createdMovement.getMovimientoId());
            }

            return createdMovement;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                logger.warn("Business rule violation creating movement: {}", e.getResponseBodyAsString());
                throw new BusinessRuleException("Error de validación: " + extractErrorMessage(e));
            }
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                logger.warn("Validation error creating movement: {}", e.getResponseBodyAsString());
                throw new ValidationException("Datos inválidos: " + extractErrorMessage(e));
            }
            
            logger.error("Error creating movement: HTTP {} - {}", 
                        e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al crear movimiento de inventario");
        } catch (Exception e) {
            logger.error("Unexpected error creating movement: {}", e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al crear movimiento", e);
        }
    }
    @Override
    public InventoryMovementDTO updateMovement(Long movimientoId, InventoryMovementDTO movementDTO) {
        logger.debug("Updating movement with ID: {}", movimientoId);

        if (movimientoId == null) {
            throw new ValidationException("El ID del movimiento es obligatorio");
        }
        if (movementDTO == null) {
            throw new ValidationException("Los datos del movimiento son obligatorios");
        }

        // Type-specific validation
        validateMovementByType(movementDTO);

        // For SALIDA movements, validate stock availability
        if ("SALIDA".equals(movementDTO.getTipo())) {
            if (!validateStockAvailability(movementDTO.getProductoId(), 
                                         movementDTO.getLoteId(), 
                                         movementDTO.getCantidad())) {
                throw new BusinessRuleException("Stock insuficiente para actualizar la salida");
            }
        }

        // Ensure the ID in the DTO matches the path parameter
        movementDTO.setMovimientoId(movimientoId);

        try {
            InventoryMovementDTO updatedMovement = webClient.put()
                    .uri("/movimientos-inventario/{id}", movimientoId)
                    .bodyValue(movementDTO)
                    .retrieve()
                    .bodyToMono(InventoryMovementDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedMovement != null) {
                // Invalidate caches since movement was modified
                invalidateMovementsCache();
                invalidateStockCache();
                
                // Update the movement cache
                String cacheKey = String.format(MOVEMENT_CACHE_KEY, movimientoId);
                cacheManager.put(cacheKey, updatedMovement, 300);
                
                logger.info("Updated movement: {} with ID: {}", 
                           updatedMovement.getCodigoMovimiento(), updatedMovement.getMovimientoId());
            }

            return updatedMovement;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Movement with ID {} not found for update", movimientoId);
                throw new EntityNotFoundException("Movimiento con ID " + movimientoId + " no encontrado");
            }
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                logger.warn("Business rule violation updating movement {}: {}", 
                           movimientoId, e.getResponseBodyAsString());
                throw new BusinessRuleException("Error de validación: " + extractErrorMessage(e));
            }
            
            logger.error("Error updating movement {}: HTTP {} - {}", 
                        movimientoId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al actualizar movimiento");
        } catch (Exception e) {
            logger.error("Unexpected error updating movement {}: {}", movimientoId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al actualizar movimiento", e);
        }
    }
    @Override
    public void deleteMovement(Long movimientoId) {
        logger.debug("Deleting movement with ID: {}", movimientoId);

        if (movimientoId == null) {
            throw new ValidationException("El ID del movimiento es obligatorio");
        }

        try {
            webClient.delete()
                    .uri("/movimientos-inventario/{id}", movimientoId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            // Invalidate caches since movement was deleted
            invalidateMovementsCache();
            invalidateStockCache();
            String cacheKey = String.format(MOVEMENT_CACHE_KEY, movimientoId);
            cacheManager.remove(cacheKey);
            
            logger.info("Deleted movement with ID: {}", movimientoId);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Movement with ID {} not found for deletion", movimientoId);
                throw new EntityNotFoundException("Movimiento con ID " + movimientoId + " no encontrado");
            }
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                logger.warn("Cannot delete movement {}: dependencies exist - {}", 
                           movimientoId, e.getResponseBodyAsString());
                throw new BusinessRuleException("No se puede eliminar el movimiento: " + extractErrorMessage(e));
            }
            
            logger.error("Error deleting movement {}: HTTP {} - {}", 
                        movimientoId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al eliminar movimiento");
        } catch (Exception e) {
            logger.error("Unexpected error deleting movement {}: {}", movimientoId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al eliminar movimiento", e);
        }
    }

    @Override
    public Page<InventoryMovementDTO> getMovementsByProduct(Long productoId, int page, int size) {
        logger.debug("Retrieving movements for product {} page {} with size {}", productoId, page, size);

        if (productoId == null) {
            throw new ValidationException("El ID del producto es obligatorio");
        }

        try {
            String uri = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("productoId", productoId)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapResponseToPage(response, page, size);

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements for product {}: HTTP {} - {}", 
                        productoId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos del producto");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements for product {}: {}", productoId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos del producto", e);
        }
    }
    @Override
    public Page<InventoryMovementDTO> getMovementsByLot(Long loteId, int page, int size) {
        logger.debug("Retrieving movements for lot {} page {} with size {}", loteId, page, size);

        if (loteId == null) {
            throw new ValidationException("El ID del lote es obligatorio");
        }

        try {
            String uri = UriComponentsBuilder.fromPath("/movimientos-inventario")
                    .queryParam("loteId", loteId)
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build()
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapResponseToPage(response, page, size);

        } catch (WebClientResponseException e) {
            logger.error("Error retrieving movements for lot {}: HTTP {} - {}", 
                        loteId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener movimientos del lote");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving movements for lot {}: {}", loteId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener movimientos del lote", e);
        }
    }

    @Override
    public String generateMovementCode(String tipo) {
        if (!StringUtils.hasText(tipo) || !isValidMovementType(tipo)) {
            throw new ValidationException("Tipo de movimiento inválido para generar código");
        }

        String prefix = TYPE_PREFIXES.get(tipo);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        
        return prefix + "-" + timestamp;
    }

    @Override
    public boolean validateStockAvailability(Long productoId, Long loteId, Integer cantidad) {
        if (productoId == null || cantidad == null || cantidad <= 0) {
            return false;
        }

        try {
            Integer availableStock = getAvailableStock(productoId, loteId);
            return availableStock != null && availableStock >= cantidad;
        } catch (Exception e) {
            logger.warn("Error validating stock availability for product {} lot {}: {}", 
                       productoId, loteId, e.getMessage());
            return false;
        }
    }
    @Override
    public Integer getAvailableStock(Long productoId, Long loteId) {
        if (productoId == null) {
            throw new ValidationException("El ID del producto es obligatorio");
        }

        // Check cache first
        String cacheKey = String.format(STOCK_CACHE_KEY, productoId, loteId != null ? loteId : 0);
        Integer cachedStock = cacheManager.get(cacheKey);
        if (cachedStock != null) {
            logger.debug("Returning cached stock for product {} lot {}: {}", productoId, loteId, cachedStock);
            return cachedStock;
        }

        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromPath("/inventario/stock-disponible")
                    .queryParam("productoId", productoId);

            if (loteId != null) {
                uriBuilder.queryParam("loteId", loteId);
            }

            String uri = uriBuilder.build().toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            Integer stock = response != null ? (Integer) response.get("cantidadDisponible") : 0;
            
            // Cache the stock for 2 minutes (stock changes frequently)
            cacheManager.put(cacheKey, stock, 120);
            
            logger.debug("Retrieved stock for product {} lot {}: {}", productoId, loteId, stock);
            return stock;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.debug("No stock found for product {} lot {}", productoId, loteId);
                return 0;
            }
            
            logger.error("Error retrieving stock for product {} lot {}: HTTP {} - {}", 
                        productoId, loteId, e.getStatusCode(), e.getResponseBodyAsString());
            throw mapHttpException(e, "Error al obtener stock disponible");
        } catch (Exception e) {
            logger.error("Unexpected error retrieving stock for product {} lot {}: {}", 
                        productoId, loteId, e.getMessage(), e);
            throw new ExternalServiceException("Error de conexión al obtener stock", e);
        }
    }

    // Helper Methods

    /**
     * Validates if the movement type is valid.
     */
    private boolean isValidMovementType(String tipo) {
        return "ENTRADA".equals(tipo) || "SALIDA".equals(tipo) || "AJUSTE".equals(tipo);
    }

    /**
     * Performs type-specific validation for movement data.
     */
    private void validateMovementByType(InventoryMovementDTO movementDTO) {
        String validationError = movementDTO.getValidationError();
        if (validationError != null) {
            throw new ValidationException(validationError);
        }

        // Additional validation can be added here
        switch (movementDTO.getTipo()) {
            case "ENTRADA":
                if (movementDTO.getPrecioUnitario() == null) {
                    throw new ValidationException("El precio unitario es obligatorio para movimientos de entrada");
                }
                break;
            case "SALIDA":
                if (movementDTO.getLoteId() == null) {
                    throw new ValidationException("El lote es obligatorio para movimientos de salida");
                }
                break;
            case "AJUSTE":
                // AJUSTE may not require loteId in some cases
                break;
        }
    }
    /**
     * Maps backend API response to Page object.
     */
    @SuppressWarnings("unchecked")
    private Page<InventoryMovementDTO> mapResponseToPage(Map<String, Object> response, int page, int size) {
        if (response == null) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
        if (content == null) {
            content = List.of();
        }

        // Convert maps to DTOs
        List<InventoryMovementDTO> movements = content.stream()
            .map(this::mapToInventoryMovementDTO)
            .toList();

        // Extract pagination info
        int totalElements = ((Number) response.getOrDefault("totalElements", 0)).intValue();
        
        return new PageImpl<>(movements, PageRequest.of(page, size), totalElements);
    }

    /**
     * Maps a map response to InventoryMovementDTO.
     */
    private InventoryMovementDTO mapToInventoryMovementDTO(Map<String, Object> map) {
        InventoryMovementDTO dto = new InventoryMovementDTO();
        
        if (map.get("movimientoId") != null) {
            dto.setMovimientoId(((Number) map.get("movimientoId")).longValue());
        }
        dto.setCodigoMovimiento((String) map.get("codigoMovimiento"));
        dto.setTipo((String) map.get("tipo"));
        
        if (map.get("productoId") != null) {
            dto.setProductoId(((Number) map.get("productoId")).longValue());
        }
        dto.setProductoNombre((String) map.get("productoNombre"));
        dto.setProductoMarca((String) map.get("productoMarca"));
        dto.setProductoTipo((String) map.get("productoTipo"));
        
        if (map.get("loteId") != null) {
            dto.setLoteId(((Number) map.get("loteId")).longValue());
        }
        dto.setLoteCodigoEntrada((String) map.get("loteCodigoEntrada"));
        
        if (map.get("cantidad") != null) {
            dto.setCantidad(((Number) map.get("cantidad")).intValue());
        }
        
        // Handle BigDecimal fields
        if (map.get("precioUnitario") != null) {
            dto.setPrecioUnitario(new java.math.BigDecimal(map.get("precioUnitario").toString()));
        }
        if (map.get("valorTotal") != null) {
            dto.setValorTotal(new java.math.BigDecimal(map.get("valorTotal").toString()));
        }
        
        dto.setDescripcion((String) map.get("descripcion"));
        
        // Handle LocalDateTime fields
        if (map.get("fechaMovimiento") != null) {
            String fechaStr = map.get("fechaMovimiento").toString();
            dto.setFechaMovimiento(LocalDateTime.parse(fechaStr));
        }
        
        dto.setUsuarioCreacion((String) map.get("usuarioCreacion"));
        
        if (map.get("ordenCompraId") != null) {
            dto.setOrdenCompraId(((Number) map.get("ordenCompraId")).longValue());
        }
        
        return dto;
    }
    /**
     * Maps HTTP exceptions to appropriate runtime exceptions.
     */
    private RuntimeException mapHttpException(WebClientResponseException e, String defaultMessage) {
        return switch (e.getStatusCode().value()) {
            case 400 -> new ValidationException("Datos inválidos: " + extractErrorMessage(e));
            case 401 -> new ExternalServiceException("No autorizado");
            case 403 -> new ExternalServiceException("Acceso denegado");
            case 404 -> new EntityNotFoundException("Recurso no encontrado");
            case 409 -> new ConflictException("Conflicto: " + extractErrorMessage(e));
            case 422 -> new BusinessRuleException("Error de reglas de negocio: " + extractErrorMessage(e));
            case 429 -> new BusinessRuleException("Límite de cuota excedido. Intente más tarde.");
            case 500 -> new ExternalServiceException("Error interno del servidor");
            case 503 -> new ExternalServiceException("Servicio no disponible. Intente más tarde.");
            default -> new ExternalServiceException(defaultMessage + ": " + e.getMessage());
        };
    }

    /**
     * Extracts error message from WebClient response.
     */
    private String extractErrorMessage(WebClientResponseException e) {
        String body = e.getResponseBodyAsString();
        if (StringUtils.hasText(body)) {
            // Try to extract message from JSON response
            if (body.contains("\"message\"")) {
                int start = body.indexOf("\"message\"");
                int colon = body.indexOf(":", start);
                int quote1 = body.indexOf("\"", colon + 1);
                int quote2 = body.indexOf("\"", quote1 + 1);
                if (quote1 > -1 && quote2 > quote1) {
                    return body.substring(quote1 + 1, quote2);
                }
            }
            return body.length() > 200 ? body.substring(0, 200) + "..." : body;
        }
        return "Error sin detalles";
    }

    /**
     * Invalidates all movement-related cache entries.
     */
    private void invalidateMovementsCache() {
        cacheManager.invalidateByPattern(MOVEMENTS_CACHE_PATTERN);
        cacheManager.invalidateByPattern(MOVEMENT_PATTERN);
        logger.debug("Invalidated movements cache due to data modification");
    }

    /**
     * Invalidates stock-related cache entries.
     */
    private void invalidateStockCache() {
        cacheManager.invalidateByPattern(STOCK_CACHE_PATTERN);
        logger.debug("Invalidated stock cache due to data modification");
    }
}
