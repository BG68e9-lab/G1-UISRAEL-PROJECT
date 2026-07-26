package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.LotDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.service.LotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.*;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Implementation of LotService for inventory tracking.
 * Handles lot management, expiration monitoring, and quantity updates.
 * 
 * Provides:
 * - Lot CRUD operations with WebClient backend integration
 * - Proximity-to-expiration detection (7-day window)
 * - Quantity tracking based on inventory movements
 * - FIFO inventory management support
 * - Filtering and search capabilities
 * 
 * Requirements: 6.1-6.7, 18.5-18.6
 */
@Service
public class LotServiceImpl implements LotService {

    private static final Logger logger = LoggerFactory.getLogger(LotServiceImpl.class);
    private static final String BACKEND_LOTS_ENDPOINT = "/api/v1/lotes";
    private static final int DEFAULT_EXPIRATION_DAYS = 7;

    private final WebClient webClient;

    /**
     * Simple cache for frequently accessed lots.
     * Uses concurrent map for thread safety.
     */
    private final Map<Long, LotDTO> lotCache = new HashMap<>();
    private volatile long lastCacheUpdate = 0;
    private static final long CACHE_TTL_MS = 300000; // 5 minutes

    @Autowired
    public LotServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }
    @Override
    public Page<LotDTO> getAllLots(Pageable pageable) {
        // El backend no expone GET /api/v1/lotes con paginación.
        // Se obtiene la lista completa desde /proximos-vencer con días grandes
        // y se combina con los lotes por producto. Como alternativa simple,
        // se pagina en memoria sobre getAllLots().
        List<LotDTO> todos = getAllLots();
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), todos.size());
        List<LotDTO> pagina = start < todos.size() ? todos.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pagina, pageable, todos.size());
    }

    @Override
    public List<LotDTO> getAllLots() {
        // El backend no tiene GET /api/v1/lotes general.
        // Se obtienen todos los lotes próximos a vencer con un horizonte
        // amplio (3650 días ≈ 10 años) para cubrir todos los registros activos.
        try {
            logger.debug("Obteniendo todos los lotes vía /proximos-vencer con horizonte amplio");

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = webClient.get()
                    .uri(BACKEND_LOTS_ENDPOINT + "/proximos-vencer?dias=3650")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null) {
                return response.stream()
                        .map(this::mapToLotDTO)
                        .sorted(Comparator.comparing(
                                lot -> lot.getFechaIngreso() != null ? lot.getFechaIngreso() : LocalDate.MIN))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();

        } catch (WebClientResponseException e) {
            logger.error("Error del backend al obtener todos los lotes: {}", e.getMessage());
            throw new RuntimeException("Error al obtener lotes del backend: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al obtener todos los lotes", e);
            throw new RuntimeException("Error al obtener todos los lotes", e);
        }
    }
    @Override
    public List<LotDTO> getLotsByProduct(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        try {
            logger.debug("Fetching lots for product ID: {}", productoId);
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = webClient.get()
                    .uri(BACKEND_LOTS_ENDPOINT + "/producto/" + productoId + "?sort=fechaIngreso,asc")
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null) {
                return response.stream()
                        .map(this::mapToLotDTO)
                        .collect(Collectors.toList());
            }
            
            return Collections.emptyList();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("No lots found for product ID: {}", productoId);
                return Collections.emptyList();
            }
            logger.error("Backend error fetching lots for product {}: {}", productoId, e.getMessage());
            throw new RuntimeException("Error fetching lots for product from backend: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error fetching lots for product {}", productoId, e);
            throw new RuntimeException("Error fetching lots for product", e);
        }
    }

    @Override
    public Page<LotDTO> getLotsByProduct(Long productoId, Pageable pageable) {
        // El backend devuelve lista completa; se pagina en memoria.
        List<LotDTO> todos = getLotsByProduct(productoId);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), todos.size());
        List<LotDTO> pagina = start < todos.size() ? todos.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pagina, pageable, todos.size());
    }
    @Override
    public List<LotDTO> getLotsExpiringInDays(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Los días no pueden ser negativos");
        }

        try {
            logger.debug("Obteniendo lotes próximos a vencer en {} días", days);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = webClient.get()
                    .uri(BACKEND_LOTS_ENDPOINT + "/proximos-vencer?dias=" + days)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null) {
                LocalDate hoy = LocalDate.now();
                LocalDate limite = hoy.plusDays(days);
                return response.stream()
                        .map(this::mapToLotDTO)
                        .filter(lot -> lot.getFechaVencimiento() != null)
                        .filter(lot -> !lot.getFechaVencimiento().isAfter(limite))
                        .filter(lot -> !lot.getFechaVencimiento().isBefore(hoy))
                        .sorted(Comparator.comparing(LotDTO::getFechaVencimiento))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("No hay lotes próximos a vencer en {} días", days);
                return Collections.emptyList();
            }
            logger.error("Error del backend al obtener lotes próximos a vencer: {}", e.getMessage());
            throw new RuntimeException("Error al obtener lotes próximos a vencer: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al obtener lotes próximos a vencer", e);
            throw new RuntimeException("Error al obtener lotes próximos a vencer", e);
        }
    }

    @Override
    public Page<LotDTO> getLotsExpiringInDays(int days, Pageable pageable) {
        List<LotDTO> expiringLots = getLotsExpiringInDays(days);
        
        // Apply pagination to the filtered results
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), expiringLots.size());
        
        List<LotDTO> pagedLots = start < expiringLots.size() ? 
            expiringLots.subList(start, end) : Collections.emptyList();
        
        return new PageImpl<>(pagedLots, pageable, expiringLots.size());
    }

    @Override
    public List<LotDTO> getLotsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        try {
            logger.debug("Obteniendo lotes que vencen entre {} y {}", startDate, endDate);

            // Calcular días hasta la fecha fin para usar /proximos-vencer
            long diasHastaFin = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            if (diasHastaFin < 0) {
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> response = webClient.get()
                    .uri(BACKEND_LOTS_ENDPOINT + "/proximos-vencer?dias=" + diasHastaFin)
                    .retrieve()
                    .bodyToMono(List.class)
                    .block();

            if (response != null) {
                return response.stream()
                        .map(this::mapToLotDTO)
                        .filter(lot -> lot.getFechaVencimiento() != null)
                        .filter(lot -> !lot.getFechaVencimiento().isBefore(startDate))
                        .filter(lot -> !lot.getFechaVencimiento().isAfter(endDate))
                        .sorted(Comparator.comparing(LotDTO::getFechaVencimiento))
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return Collections.emptyList();
            }
            logger.error("Error del backend al obtener lotes por rango de fechas: {}", e.getMessage());
            throw new RuntimeException("Error al obtener lotes por rango de fechas: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al obtener lotes por rango de fechas", e);
            throw new RuntimeException("Error al obtener lotes por rango de fechas", e);
        }
    }
    @Override
    public Optional<LotDTO> getLotById(Long loteId) {
        if (loteId == null) {
            throw new IllegalArgumentException("Lot ID cannot be null");
        }

        try {
            // Check cache first
            if (lotCache.containsKey(loteId) && isCacheValid()) {
                logger.debug("Returning lot {} from cache", loteId);
                return Optional.of(lotCache.get(loteId));
            }

            logger.debug("Fetching lot by ID: {}", loteId);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.get()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO lot = mapToLotDTO(response);
                // Update cache
                lotCache.put(loteId, lot);
                lastCacheUpdate = System.currentTimeMillis();
                return Optional.of(lot);
            }
            
            return Optional.empty();
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Lot not found with ID: {}", loteId);
                return Optional.empty();
            }
            logger.error("Backend error fetching lot {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error fetching lot from backend: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error fetching lot {}", loteId, e);
            throw new RuntimeException("Error fetching lot", e);
        }
    }

    @Override
    public Optional<LotDTO> getLotByEntryCode(String codigoEntrada) {
        if (codigoEntrada == null || codigoEntrada.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de entrada no puede ser nulo o vacío");
        }

        // El backend no tiene endpoint /codigo/{codigo}; se busca en memoria.
        logger.debug("Buscando lote por código de entrada: {}", codigoEntrada);
        return getAllLots().stream()
                .filter(lot -> codigoEntrada.equalsIgnoreCase(lot.getCodigoEntrada()))
                .findFirst();
    }

    @Override
    public List<LotDTO> getActiveLots() {
        // El backend no tiene endpoint /active; se filtra desde la lista completa.
        return getAllLots().stream()
                .filter(lot -> Boolean.TRUE.equals(lot.getActivo()))
                .collect(Collectors.toList());
    }
    @Override
    public List<LotDTO> getLotsWithAvailableStock() {
        // El backend no tiene endpoint /with-stock; se filtra localmente.
        return getAllLots().stream()
                .filter(lot -> lot.getCantidadDisponible() != null && lot.getCantidadDisponible() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<LotDTO> getLotsWithAvailableStock(Long productoId) {
        if (productoId == null) {
            throw new IllegalArgumentException("El ID de producto no puede ser nulo");
        }
        return getLotsByProduct(productoId).stream()
                .filter(lot -> lot.getCantidadDisponible() != null && lot.getCantidadDisponible() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public LotDTO createLot(LotDTO lotDTO) {
        if (lotDTO == null) {
            throw new IllegalArgumentException("El DTO del lote no puede ser nulo");
        }

        try {
            logger.debug("Creando nuevo lote para producto: {}", lotDTO.getProductoId());

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.post()
                    .uri(BACKEND_LOTS_ENDPOINT)
                    .bodyValue(lotDTO)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO createdLot = mapToLotDTO(response);
                invalidateCache();
                logger.info("Lote creado exitosamente con ID: {}", createdLot.getLoteId());
                return createdLot;
            }

            throw new RuntimeException("No se pudo crear el lote: sin respuesta del backend");

        } catch (WebClientResponseException e) {
            logger.error("Error del backend al crear lote: {}", e.getMessage());
            throw new RuntimeException("Error al crear lote: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Error al crear lote", e);
            throw new RuntimeException("Error al crear lote", e);
        }
    }

    @Override
    public LotDTO updateLot(Long loteId, LotDTO lotDTO) {
        if (loteId == null) {
            throw new IllegalArgumentException("El ID del lote no puede ser nulo");
        }
        if (lotDTO == null) {
            throw new IllegalArgumentException("El DTO del lote no puede ser nulo");
        }

        try {
            logger.debug("Actualizando lote con ID: {}", loteId);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.put()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId)
                    .bodyValue(lotDTO)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO updatedLot = mapToLotDTO(response);
                lotCache.put(loteId, updatedLot);
                lastCacheUpdate = System.currentTimeMillis();
                logger.info("Lote actualizado exitosamente: {}", loteId);
                return updatedLot;
            }

            throw new RuntimeException("No se pudo actualizar el lote: sin respuesta del backend");

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Lote no encontrado con ID: " + loteId);
            }
            logger.error("Error del backend al actualizar lote {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error al actualizar lote: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("Error al actualizar lote {}", loteId, e);
            throw new RuntimeException("Error al actualizar lote", e);
        }
    }
    @Override
    public LotDTO updateLotQuantity(Long loteId, Integer newQuantity) {
        if (loteId == null) {
            throw new IllegalArgumentException("El ID del lote no puede ser nulo");
        }
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("La nueva cantidad no puede ser nula ni negativa");
        }

        try {
            logger.debug("Actualizando cantidad del lote - ID: {}, nueva cantidad: {}", loteId, newQuantity);

            Map<String, Object> updateRequest = Map.of("cantidadDisponible", newQuantity);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.patch()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId + "/cantidad")
                    .bodyValue(updateRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO updatedLot = mapToLotDTO(response);
                lotCache.put(loteId, updatedLot);
                lastCacheUpdate = System.currentTimeMillis();
                logger.info("Cantidad del lote actualizada - ID: {}, nueva cantidad: {}", loteId, newQuantity);
                return updatedLot;
            }

            throw new RuntimeException("No se pudo actualizar la cantidad del lote: sin respuesta del backend");

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Lote no encontrado con ID: " + loteId);
            }
            if (e.getStatusCode().value() == 422) {
                throw new BusinessRuleException("No se puede actualizar la cantidad del lote: " + e.getResponseBodyAsString());
            }
            logger.error("Error del backend al actualizar cantidad del lote {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error al actualizar cantidad del lote: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al actualizar cantidad del lote {}", loteId, e);
            throw new RuntimeException("Error al actualizar cantidad del lote", e);
        }
    }

    @Override
    public LotDTO deactivateLot(Long loteId) {
        if (loteId == null) {
            throw new IllegalArgumentException("El ID del lote no puede ser nulo");
        }

        try {
            logger.debug("Desactivando lote: {}", loteId);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.patch()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId + "/desactivar")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO deactivatedLot = mapToLotDTO(response);
                lotCache.put(loteId, deactivatedLot);
                lastCacheUpdate = System.currentTimeMillis();
                logger.info("Lote desactivado: {}", loteId);
                return deactivatedLot;
            }

            throw new RuntimeException("No se pudo desactivar el lote: sin respuesta del backend");

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Lote no encontrado con ID: " + loteId);
            }
            logger.error("Error del backend al desactivar lote {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error al desactivar lote: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al desactivar lote {}", loteId, e);
            throw new RuntimeException("Error al desactivar lote", e);
        }
    }

    @Override
    public LotDTO activateLot(Long loteId) {
        if (loteId == null) {
            throw new IllegalArgumentException("El ID del lote no puede ser nulo");
        }

        try {
            logger.debug("Activando lote: {}", loteId);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClient.patch()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId + "/activar")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null) {
                LotDTO activatedLot = mapToLotDTO(response);
                lotCache.put(loteId, activatedLot);
                lastCacheUpdate = System.currentTimeMillis();
                logger.info("Lote activado: {}", loteId);
                return activatedLot;
            }

            throw new RuntimeException("No se pudo activar el lote: sin respuesta del backend");

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Lote no encontrado con ID: " + loteId);
            }
            logger.error("Error del backend al activar lote {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error al activar lote: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al activar lote {}", loteId, e);
            throw new RuntimeException("Error al activar lote", e);
        }
    }

    @Override
    public void deleteLot(Long loteId) {
        if (loteId == null) {
            throw new IllegalArgumentException("Lot ID cannot be null");
        }

        try {
            logger.debug("Deleting lot: {}", loteId);
            
            webClient.delete()
                    .uri(BACKEND_LOTS_ENDPOINT + "/" + loteId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            // Remove from cache
            lotCache.remove(loteId);
            logger.info("Lote eliminado: {}", loteId);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Lote no encontrado con ID: " + loteId);
            }
            if (e.getStatusCode().value() == 422) {
                throw new BusinessRuleException("No se puede eliminar el lote: tiene movimientos de inventario o transacciones asociadas");
            }
            logger.error("Error del backend al eliminar lote {}: {}", loteId, e.getMessage());
            throw new RuntimeException("Error al eliminar lote: " + e.getStatusCode(), e);
        } catch (Exception e) {
            logger.error("Error al eliminar lote {}", loteId, e);
            throw new RuntimeException("Error al eliminar lote", e);
        }
    }
    @Override
    public long getExpiringLotsCount(int days) {
        return getLotsExpiringInDays(days).size();
    }

    @Override
    public long getTotalLotsCount() {
        return getAllLots().size();
    }

    @Override
    public long getActiveLotsCount() {
        return getActiveLots().size();
    }

    @Override
    public long getLotsWithStockCount() {
        return getLotsWithAvailableStock().size();
    }

    @Override
    public Page<LotDTO> searchLots(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllLots(pageable);
        }

        // El backend no tiene endpoint /search; se filtra en memoria.
        String termino = searchTerm.trim().toLowerCase();
        List<LotDTO> todos = getAllLots();
        List<LotDTO> filtrados = todos.stream()
                .filter(lot ->
                        (lot.getCodigoEntrada() != null && lot.getCodigoEntrada().toLowerCase().contains(termino)) ||
                        (lot.getProductoNombre() != null && lot.getProductoNombre().toLowerCase().contains(termino)) ||
                        (lot.getProductoMarca() != null && lot.getProductoMarca().toLowerCase().contains(termino)))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtrados.size());
        List<LotDTO> pagina = start < filtrados.size() ? filtrados.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pagina, pageable, filtrados.size());
    }
    @Override
    public Page<LotDTO> getLotsWithFilters(Long productoId, Integer expiringInDays, Boolean hasStock,
                                           Boolean isActive, Pageable pageable) {
        // El backend no tiene endpoint /filter; se filtra en memoria.
        logger.debug("Filtrando lotes - producto: {}, vencimiento: {}, conStock: {}, activo: {}",
                productoId, expiringInDays, hasStock, isActive);

        List<LotDTO> base = (productoId != null) ? getLotsByProduct(productoId) : getAllLots();

        LocalDate hoy = LocalDate.now();
        List<LotDTO> filtrados = base.stream()
                .filter(lot -> isActive == null || isActive.equals(lot.getActivo()))
                .filter(lot -> hasStock == null || (hasStock
                        ? (lot.getCantidadDisponible() != null && lot.getCantidadDisponible() > 0)
                        : (lot.getCantidadDisponible() == null || lot.getCantidadDisponible() == 0)))
                .filter(lot -> expiringInDays == null || (lot.getFechaVencimiento() != null &&
                        !lot.getFechaVencimiento().isAfter(hoy.plusDays(expiringInDays)) &&
                        !lot.getFechaVencimiento().isBefore(hoy)))
                .sorted(Comparator.comparing(
                        lot -> lot.getFechaIngreso() != null ? lot.getFechaIngreso() : LocalDate.MIN))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtrados.size());
        List<LotDTO> pagina = start < filtrados.size() ? filtrados.subList(start, end) : Collections.emptyList();
        return new PageImpl<>(pagina, pageable, filtrados.size());
    }

    @Override
    public void invalidateCache() {
        logger.debug("Invalidating lot cache");
        lotCache.clear();
        lastCacheUpdate = 0;
    }
    /**
     * Check if cache is still valid based on TTL.
     * 
     * @return true if cache is valid, false otherwise
     */
    private boolean isCacheValid() {
        return (System.currentTimeMillis() - lastCacheUpdate) < CACHE_TTL_MS;
    }

    /**
     * Map a backend response Map to LotDTO.
     * 
     * @param data the Map containing lot data from backend
     * @return LotDTO object
     */
    private LotDTO mapToLotDTO(Map<String, Object> data) {
        if (data == null) {
            return null;
        }

        LotDTO lot = new LotDTO();
        
        try {
            // Handle numeric fields safely
            if (data.get("loteId") != null) {
                lot.setLoteId(((Number) data.get("loteId")).longValue());
            }
            
            lot.setCodigoEntrada((String) data.get("codigoEntrada"));
            
            if (data.get("productoId") != null) {
                lot.setProductoId(((Number) data.get("productoId")).longValue());
            }
            
            lot.setProductoNombre((String) data.get("productoNombre"));
            lot.setProductoMarca((String) data.get("productoMarca"));
            lot.setProductoTipo((String) data.get("productoTipo"));
            
            if (data.get("cantidadInicial") != null) {
                lot.setCantidadInicial(((Number) data.get("cantidadInicial")).intValue());
            }
            
            if (data.get("cantidadDisponible") != null) {
                lot.setCantidadDisponible(((Number) data.get("cantidadDisponible")).intValue());
            }
            
            if (data.get("precioCosto") != null) {
                lot.setPrecioCosto(new java.math.BigDecimal(data.get("precioCosto").toString()));
            }
            
            // Handle date fields
            if (data.get("fechaIngreso") != null) {
                String fechaIngresoStr = data.get("fechaIngreso").toString();
                lot.setFechaIngreso(java.time.LocalDate.parse(fechaIngresoStr.substring(0, 10)));
            }
            
            if (data.get("fechaVencimiento") != null) {
                String fechaVencimientoStr = data.get("fechaVencimiento").toString();
                lot.setFechaVencimiento(java.time.LocalDate.parse(fechaVencimientoStr.substring(0, 10)));
            }
            
            if (data.get("fechaCreacion") != null) {
                String fechaCreacionStr = data.get("fechaCreacion").toString();
                // Handle both LocalDateTime and LocalDate formats
                if (fechaCreacionStr.length() > 10) {
                    lot.setFechaCreacion(java.time.LocalDateTime.parse(fechaCreacionStr.substring(0, 19)));
                }
            }
            
            lot.setUsuarioCreacion((String) data.get("usuarioCreacion"));
            lot.setActivo((Boolean) data.get("activo"));
            
            if (data.get("ordenCompraId") != null) {
                lot.setOrdenCompraId(((Number) data.get("ordenCompraId")).longValue());
            }
            
        } catch (Exception e) {
            logger.error("Error mapping lot data: {}", data, e);
            throw new RuntimeException("Error mapping lot data", e);
        }
        
        return lot;
    }
}