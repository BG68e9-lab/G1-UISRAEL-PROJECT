package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.AlertDTO;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.AlertService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AlertService using WebClient for backend API communication.
 * Provides alert management with filtering by type and read status.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Alert filtering by type (STOCK_BAJO, VENCIMIENTO_PROXIMO, SISTEMA, USUARIO, NEGOCIO)
 * - Mark as read/unread functionality with user tracking
 * - Priority-based sorting (ALTA, MEDIA, BAJA)
 * - Pagination support for large alert lists
 * - Unread alerts counting for dashboard badges
 * - Auto-expiration handling and cleanup
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 10.1-10.12, 18.5-18.6
 */
@Service
public class AlertServiceImpl implements AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertServiceImpl.class);
    
    private static final String ALERTS_ENDPOINT = "/api/v1/alertas";
    private static final String ALERT_BY_ID_ENDPOINT = "/api/v1/alertas/{id}";
    private static final String ALERTS_BY_TYPE_ENDPOINT = "/api/v1/alertas/tipo/{tipo}";
    private static final String ALERTS_BY_READ_STATUS_ENDPOINT = "/api/v1/alertas/leido/{leido}";
    private static final String ALERTS_FILTERED_ENDPOINT = "/api/v1/alertas/filtro";
    private static final String ALERT_MARK_READ_ENDPOINT = "/api/v1/alertas/{id}/marcar-leido";
    private static final String ALERT_MARK_UNREAD_ENDPOINT = "/api/v1/alertas/{id}/marcar-no-leido";
    private static final String ALERTS_MARK_READ_BULK_ENDPOINT = "/api/v1/alertas/marcar-leido/masivo";
    private static final String ALERTS_COUNT_UNREAD_ENDPOINT = "/api/v1/alertas/contar/no-leidas";
    private static final String ALERTS_COUNT_UNREAD_BY_TYPE_ENDPOINT = "/api/v1/alertas/contar/no-leidas/tipo/{tipo}";
    private static final String ALERTS_DASHBOARD_ENDPOINT = "/api/v1/alertas/dashboard";
    private static final String ALERTS_BY_PRIORITY_ENDPOINT = "/api/v1/alertas/prioridad/{prioridad}";
    private static final String ALERTS_BY_ENTITY_ENDPOINT = "/api/v1/alertas/entidad";
    private static final String ALERTS_CLEAN_EXPIRED_ENDPOINT = "/api/v1/alertas/limpiar-expiradas";
    private static final String ALERT_EXISTS_ENDPOINT = "/api/v1/alertas/{id}/existe";

    private final WebClient webClient;
    private final Retry retryConfiguration;

    @Autowired
    public AlertServiceImpl(WebClient webClient, Retry retryConfiguration) {
        this.webClient = webClient;
        this.retryConfiguration = retryConfiguration;
    }

    @Override
    public Page<AlertDTO> getAllAlerts(Pageable pageable) {
        logger.debug("Retrieving all alerts with pagination: page={}, size={}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving alerts: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving alerts from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AlertDTO> getAlertsByType(String tipo, Pageable pageable) {
        logger.debug("Retrieving alerts by type: {} with pagination: page={}, size={}", 
                    tipo, pageable.getPageNumber(), pageable.getPageSize());
        
        if (tipo == null || tipo.trim().isEmpty()) {
            return getAllAlerts(pageable);
        }

        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_BY_TYPE_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(tipo)
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving alerts by type %s: %s - %s", 
                    tipo, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving alerts by type " + tipo;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AlertDTO> getAlertsByReadStatus(boolean leido, Pageable pageable) {
        logger.debug("Retrieving alerts by read status: {} with pagination: page={}, size={}", 
                    leido, pageable.getPageNumber(), pageable.getPageSize());

        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_BY_READ_STATUS_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(leido)
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving alerts by read status %s: %s - %s", 
                    leido, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving alerts by read status " + leido;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AlertDTO> getAlertsByTypeAndReadStatus(String tipo, Boolean leido, Pageable pageable) {
        logger.debug("Retrieving alerts by type: {} and read status: {} with pagination: page={}, size={}", 
                    tipo, leido, pageable.getPageNumber(), pageable.getPageSize());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(ALERTS_FILTERED_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable));
            
            if (tipo != null && !tipo.trim().isEmpty()) {
                builder.queryParam("tipo", tipo.trim());
            }
            
            if (leido != null) {
                builder.queryParam("leido", leido);
            }

            String uri = builder.toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving filtered alerts: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving filtered alerts";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public AlertDTO getAlertById(Long id) {
        logger.debug("Retrieving alert with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }

        try {
            AlertDTO alert = webClient.get()
                    .uri(ALERT_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(AlertDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (alert != null) {
                logger.debug("Retrieved alert {} from backend", id);
                return alert;
            } else {
                throw new EntityNotFoundException("Alert not found with ID: " + id);
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Alert not found with ID: " + id);
            } else {
                String errorMsg = String.format("Backend API error while retrieving alert %d: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving alert " + id + " from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public AlertDTO createAlert(AlertDTO alertDTO) {
        logger.debug("Creating new alert: {}", alertDTO.getTitulo());
        
        if (alertDTO == null) {
            throw new IllegalArgumentException("AlertDTO cannot be null");
        }

        if (alertDTO.getAlertaId() != null) {
            throw new IllegalArgumentException("Alert ID must be null for creation");
        }

        try {
            AlertDTO createdAlert = webClient.post()
                    .uri(ALERTS_ENDPOINT)
                    .bodyValue(alertDTO)
                    .retrieve()
                    .bodyToMono(AlertDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdAlert != null) {
                logger.info("Successfully created alert: {} with ID: {}", 
                           createdAlert.getTitulo(), createdAlert.getAlertaId());
                return createdAlert;
            } else {
                throw new RuntimeException("Backend returned null after alert creation");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 400) {
                throw new ValidationException("Invalid alert data: " + e.getResponseBodyAsString());
            } else {
                String errorMsg = String.format("Backend API error while creating alert: %s - %s", 
                        e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while creating alert: " + alertDTO.getTitulo();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public AlertDTO markAlertAsRead(Long id, String usuarioEmail) {
        logger.debug("Marking alert {} as read by user: {}", id, usuarioEmail);
        
        if (id == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }
        
        if (usuarioEmail == null || usuarioEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("usuarioEmail", usuarioEmail.trim());

            AlertDTO updatedAlert = webClient.put()
                    .uri(ALERT_MARK_READ_ENDPOINT, id)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AlertDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedAlert != null) {
                logger.info("Successfully marked alert {} as read by user: {}", id, usuarioEmail);
                return updatedAlert;
            } else {
                throw new RuntimeException("Backend returned null after marking alert as read");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Alert not found with ID: " + id);
            } else {
                String errorMsg = String.format("Backend API error while marking alert %d as read: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while marking alert " + id + " as read";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public AlertDTO markAlertAsUnread(Long id) {
        logger.debug("Marking alert {} as unread", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }

        try {
            AlertDTO updatedAlert = webClient.put()
                    .uri(ALERT_MARK_UNREAD_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(AlertDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedAlert != null) {
                logger.info("Successfully marked alert {} as unread", id);
                return updatedAlert;
            } else {
                throw new RuntimeException("Backend returned null after marking alert as unread");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Alert not found with ID: " + id);
            } else {
                String errorMsg = String.format("Backend API error while marking alert %d as unread: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while marking alert " + id + " as unread";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<AlertDTO> markAlertsAsRead(List<Long> alertIds, String usuarioEmail) {
        logger.debug("Marking {} alerts as read by user: {}", alertIds.size(), usuarioEmail);
        
        if (alertIds == null || alertIds.isEmpty()) {
            throw new IllegalArgumentException("Alert IDs list cannot be null or empty");
        }
        
        if (usuarioEmail == null || usuarioEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be null or empty");
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("alertIds", alertIds);
            requestBody.put("usuarioEmail", usuarioEmail.trim());

            List<AlertDTO> updatedAlerts = webClient.put()
                    .uri(ALERTS_MARK_READ_BULK_ENDPOINT)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AlertDTO>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedAlerts != null) {
                logger.info("Successfully marked {} alerts as read by user: {}", updatedAlerts.size(), usuarioEmail);
                return updatedAlerts;
            } else {
                throw new RuntimeException("Backend returned null after bulk marking alerts as read");
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while bulk marking alerts as read: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while bulk marking alerts as read";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void deleteAlert(Long id) {
        logger.debug("Deleting alert with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Alert ID cannot be null");
        }

        try {
            webClient.delete()
                    .uri(ALERT_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            logger.info("Successfully deleted alert with ID: {}", id);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Alert not found with ID: " + id);
            } else {
                String errorMsg = String.format("Backend API error while deleting alert %d: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while deleting alert " + id;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public long countUnreadAlerts() {
        logger.debug("Counting unread alerts");

        try {
            Long count = webClient.get()
                    .uri(ALERTS_COUNT_UNREAD_ENDPOINT)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .retryWhen(retryConfiguration)
                    .block();

            long result = count != null ? count : 0L;
            logger.debug("Found {} unread alerts", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error counting unread alerts: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    public long countUnreadAlertsByType(String tipo) {
        logger.debug("Counting unread alerts by type: {}", tipo);
        
        if (tipo == null || tipo.trim().isEmpty()) {
            return countUnreadAlerts();
        }

        try {
            Long count = webClient.get()
                    .uri(ALERTS_COUNT_UNREAD_BY_TYPE_ENDPOINT, tipo)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .retryWhen(retryConfiguration)
                    .block();

            long result = count != null ? count : 0L;
            logger.debug("Found {} unread alerts of type {}", result, tipo);
            return result;
            
        } catch (Exception e) {
            logger.error("Error counting unread alerts by type {}: {}", tipo, e.getMessage());
            return 0L;
        }
    }

    @Override
    public List<AlertDTO> getUnreadAlertsForDashboard(int limit) {
        logger.debug("Retrieving {} unread alerts for dashboard", limit);

        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_DASHBOARD_ENDPOINT)
                    .queryParam("limit", limit)
                    .toUriString();

            List<AlertDTO> alerts = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AlertDTO>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (alerts != null) {
                logger.debug("Retrieved {} unread alerts for dashboard", alerts.size());
                return alerts;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving unread alerts for dashboard: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public Page<AlertDTO> getAlertsByPriority(String prioridad, Pageable pageable) {
        logger.debug("Retrieving alerts by priority: {} with pagination: page={}, size={}", 
                    prioridad, pageable.getPageNumber(), pageable.getPageSize());

        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_BY_PRIORITY_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(prioridad)
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving alerts by priority %s: %s - %s", 
                    prioridad, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving alerts by priority " + prioridad;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AlertDTO> getAlertsByEntity(String entidadTipo, Long entidadId, Pageable pageable) {
        logger.debug("Retrieving alerts by entity: {}:{} with pagination: page={}, size={}", 
                    entidadTipo, entidadId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            String uri = UriComponentsBuilder.fromPath(ALERTS_BY_ENTITY_ENDPOINT)
                    .queryParam("entidadTipo", entidadTipo)
                    .queryParam("entidadId", entidadId)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .toUriString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving alerts by entity %s:%d: %s - %s", 
                    entidadTipo, entidadId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Unexpected error while retrieving alerts by entity %s:%d", 
                    entidadTipo, entidadId);
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public int cleanExpiredAlerts() {
        logger.debug("Cleaning expired alerts");

        try {
            Integer count = webClient.delete()
                    .uri(ALERTS_CLEAN_EXPIRED_ENDPOINT)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .retryWhen(retryConfiguration)
                    .block();

            int result = count != null ? count : 0;
            logger.info("Cleaned {} expired alerts", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error cleaning expired alerts: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean alertExists(Long id) {
        if (id == null) {
            return false;
        }

        try {
            Boolean exists = webClient.get()
                    .uri(ALERT_EXISTS_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(exists);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return false;
            }
            logger.error("Error checking alert existence for ID {}: {}", id, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error checking alert existence for ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    /**
     * Builds sort parameter string from Pageable.
     * 
     * @param pageable the pagination information
     * @return sort parameter string
     */
    private String buildSortParameter(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().toString().replace(": ", ",");
        } else {
            // Default sorting: priority (ALTA first), then creation date (newest first)
            return "prioridad,asc&sort=fechaCreacion,desc";
        }
    }

    /**
     * Maps backend paginated response to Spring Page object.
     * 
     * @param response the backend response
     * @param pageable the pagination request
     * @return mapped Page object
     */
    @SuppressWarnings("unchecked")
    private Page<AlertDTO> mapPageResponse(Map<String, Object> response, Pageable pageable) {
        if (response == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        try {
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            Number totalElements = (Number) response.get("totalElements");
            
            List<AlertDTO> alerts = content != null ? 
                content.stream().map(this::mapToAlertDTO).toList() : List.of();
            
            long total = totalElements != null ? totalElements.longValue() : 0L;
            
            return new PageImpl<>(alerts, pageable, total);
            
        } catch (Exception e) {
            logger.error("Error mapping page response: {}", e.getMessage());
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    /**
     * Maps a Map to AlertDTO object.
     * This is a simplified mapping - in a real application, you might use a library like MapStruct.
     * 
     * @param map the map from backend response
     * @return mapped AlertDTO
     */
    private AlertDTO mapToAlertDTO(Map<String, Object> map) {
        AlertDTO alert = new AlertDTO();
        
        try {
            // Map basic fields
            if (map.get("alertaId") != null) {
                alert.setAlertaId(((Number) map.get("alertaId")).longValue());
            }
            
            alert.setTitulo((String) map.get("titulo"));
            alert.setMensaje((String) map.get("mensaje"));
            alert.setTipo((String) map.get("tipo"));
            alert.setPrioridad((String) map.get("prioridad"));
            
            if (map.get("leido") != null) {
                alert.setLeido((Boolean) map.get("leido"));
            }
            
            alert.setEntidadTipo((String) map.get("entidadTipo"));
            
            if (map.get("entidadId") != null) {
                alert.setEntidadId(((Number) map.get("entidadId")).longValue());
            }
            
            if (map.get("negocioId") != null) {
                alert.setNegocioId(((Number) map.get("negocioId")).longValue());
            }
            
            if (map.get("usuarioId") != null) {
                alert.setUsuarioId(((Number) map.get("usuarioId")).longValue());
            }
            
            alert.setUsuarioLeido((String) map.get("usuarioLeido"));
            
            if (map.get("activo") != null) {
                alert.setActivo((Boolean) map.get("activo"));
            }
            
            // Map date fields
            if (map.get("fechaCreacion") != null) {
                String fechaCreacionStr = (String) map.get("fechaCreacion");
                alert.setFechaCreacion(LocalDateTime.parse(fechaCreacionStr));
            }
            
            if (map.get("fechaLeido") != null) {
                String fechaLeidoStr = (String) map.get("fechaLeido");
                alert.setFechaLeido(LocalDateTime.parse(fechaLeidoStr));
            }
            
            if (map.get("fechaExpiracion") != null) {
                String fechaExpiracionStr = (String) map.get("fechaExpiracion");
                alert.setFechaExpiracion(LocalDateTime.parse(fechaExpiracionStr));
            }
            
        } catch (Exception e) {
            logger.error("Error mapping alert from backend response: {}", e.getMessage());
            // Return a basic alert with available data
            alert.setTitulo((String) map.getOrDefault("titulo", "Unknown Alert"));
            alert.setMensaje((String) map.getOrDefault("mensaje", "No message available"));
            alert.setTipo((String) map.getOrDefault("tipo", "SISTEMA"));
            alert.setPrioridad((String) map.getOrDefault("prioridad", "MEDIA"));
        }
        
        return alert;
    }
}