package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.AuditLogDTO;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.service.AuditService;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of AuditService using WebClient for backend API communication.
 * Provides comprehensive audit log retrieval capabilities with filtering options
 * and detailed change tracking for system transparency and compliance.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Advanced filtering by entidad, accion, and date range
 * - Pagination support for large audit log datasets
 * - Detailed audit log information with JSON change display
 * - User-based and entity-based audit trail tracking
 * - Export functionality for compliance reporting
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 11.1-11.7, 18.5-18.6
 */
@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    
    private static final String AUDIT_LOGS_ENDPOINT = "/api/v1/auditoria";
    private static final String AUDIT_LOG_BY_ID_ENDPOINT = "/api/v1/auditoria/{id}";
    private static final String AUDIT_LOGS_BY_ENTIDAD_ENDPOINT = "/api/v1/auditoria/entidad/{entidad}";
    private static final String AUDIT_LOGS_BY_ACCION_ENDPOINT = "/api/v1/auditoria/accion/{accion}";
    private static final String AUDIT_LOGS_BY_DATE_RANGE_ENDPOINT = "/api/v1/auditoria/rango-fecha";
    private static final String AUDIT_LOGS_FILTERED_ENDPOINT = "/api/v1/auditoria/filtro";
    private static final String AUDIT_LOGS_BY_ENTITY_ENDPOINT = "/api/v1/auditoria/entidad";
    private static final String AUDIT_LOGS_BY_USER_ENDPOINT = "/api/v1/auditoria/usuario/{usuarioId}";
    private static final String AUDIT_LOGS_RECENT_ENDPOINT = "/api/v1/auditoria/reciente";
    private static final String AUDIT_STATISTICS_ENDPOINT = "/api/v1/auditoria/estadisticas";
    private static final String AUDIT_ENTIDADES_ENDPOINT = "/api/v1/auditoria/entidades/distintas";
    private static final String AUDIT_ACCIONES_ENDPOINT = "/api/v1/auditoria/acciones/distintas";

    private final WebClient webClient;
    private final Retry retryConfiguration;

    @Autowired
    public AuditServiceImpl(WebClient webClient, Retry retryConfiguration) {
        this.webClient = webClient;
        this.retryConfiguration = retryConfiguration;
    }
    @Override
    public Page<AuditLogDTO> getAllAuditLogs(Pageable pageable) {
        logger.debug("Retrieving all audit logs with pagination: page={}, size={}", 
                    pageable.getPageNumber(), pageable.getPageSize());
        
        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_ENDPOINT)
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
            String errorMsg = String.format("Backend API error while retrieving audit logs: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit logs from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByEntidad(String entidad, Pageable pageable) {
        logger.debug("Retrieving audit logs by entidad: {} with pagination: page={}, size={}", 
                    entidad, pageable.getPageNumber(), pageable.getPageSize());
        
        if (entidad == null || entidad.trim().isEmpty()) {
            return getAllAuditLogs(pageable);
        }

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_BY_ENTIDAD_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(entidad.trim().toUpperCase())
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving audit logs by entidad %s: %s - %s", 
                    entidad, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit logs by entidad " + entidad;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    @Override
    public Page<AuditLogDTO> getAuditLogsByAccion(String accion, Pageable pageable) {
        logger.debug("Retrieving audit logs by accion: {} with pagination: page={}, size={}", 
                    accion, pageable.getPageNumber(), pageable.getPageSize());
        
        if (accion == null || accion.trim().isEmpty()) {
            return getAllAuditLogs(pageable);
        }

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_BY_ACCION_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(accion.trim().toUpperCase())
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving audit logs by accion %s: %s - %s", 
                    accion, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit logs by accion " + accion;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByDateRange(LocalDateTime fechaInicio, 
                                                    LocalDateTime fechaFin, 
                                                    Pageable pageable) {
        logger.debug("Retrieving audit logs by date range: {} to {} with pagination: page={}, size={}", 
                    fechaInicio, fechaFin, pageable.getPageNumber(), pageable.getPageSize());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(AUDIT_LOGS_BY_DATE_RANGE_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable));
            
            if (fechaInicio != null) {
                builder.queryParam("fechaInicio", fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            if (fechaFin != null) {
                builder.queryParam("fechaFin", fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
            String errorMsg = String.format("Backend API error while retrieving audit logs by date range: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit logs by date range";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    @Override
    public Page<AuditLogDTO> getAuditLogsFiltered(String entidad, 
                                                 String accion, 
                                                 LocalDateTime fechaInicio, 
                                                 LocalDateTime fechaFin, 
                                                 Pageable pageable) {
        logger.debug("Retrieving filtered audit logs: entidad={}, accion={}, fechaInicio={}, fechaFin={} with pagination: page={}, size={}", 
                    entidad, accion, fechaInicio, fechaFin, pageable.getPageNumber(), pageable.getPageSize());

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(AUDIT_LOGS_FILTERED_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable));
            
            if (entidad != null && !entidad.trim().isEmpty()) {
                builder.queryParam("entidad", entidad.trim().toUpperCase());
            }
            
            if (accion != null && !accion.trim().isEmpty()) {
                builder.queryParam("accion", accion.trim().toUpperCase());
            }
            
            if (fechaInicio != null) {
                builder.queryParam("fechaInicio", fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            if (fechaFin != null) {
                builder.queryParam("fechaFin", fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
            String errorMsg = String.format("Backend API error while retrieving filtered audit logs: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving filtered audit logs";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public AuditLogDTO getAuditLogDetail(Long logId) {
        logger.debug("Retrieving audit log detail with ID: {}", logId);
        
        if (logId == null) {
            throw new IllegalArgumentException("Audit log ID cannot be null");
        }

        try {
            AuditLogDTO auditLog = webClient.get()
                    .uri(AUDIT_LOG_BY_ID_ENDPOINT, logId)
                    .retrieve()
                    .bodyToMono(AuditLogDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (auditLog != null) {
                logger.debug("Retrieved audit log {} from backend", logId);
                return auditLog;
            } else {
                throw new EntityNotFoundException("Audit log not found with ID: " + logId);
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Audit log {} not found in backend", logId);
                throw new EntityNotFoundException("Audit log not found with ID: " + logId);
            } else {
                String errorMsg = String.format("Backend API error while retrieving audit log %d: %s - %s", 
                        logId, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit log " + logId + " from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    @Override
    public Page<AuditLogDTO> getAuditLogsByEntity(String entidad, Long entidadId, Pageable pageable) {
        logger.debug("Retrieving audit logs by entity: {}:{} with pagination: page={}, size={}", 
                    entidad, entidadId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_BY_ENTITY_ENDPOINT)
                    .queryParam("entidad", entidad != null ? entidad.trim().toUpperCase() : "")
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
            String errorMsg = String.format("Backend API error while retrieving audit logs by entity %s:%d: %s - %s", 
                    entidad, entidadId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Unexpected error while retrieving audit logs by entity %s:%d", 
                    entidad, entidadId);
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<AuditLogDTO> getAuditLogsByUser(Long usuarioId, Pageable pageable) {
        logger.debug("Retrieving audit logs by user: {} with pagination: page={}, size={}", 
                    usuarioId, pageable.getPageNumber(), pageable.getPageSize());
        
        if (usuarioId == null) {
            throw new IllegalArgumentException("Usuario ID cannot be null");
        }

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_BY_USER_ENDPOINT)
                    .queryParam("page", pageable.getPageNumber())
                    .queryParam("size", pageable.getPageSize())
                    .queryParam("sort", buildSortParameter(pageable))
                    .build(usuarioId)
                    .toString();

            Map<String, Object> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            return mapPageResponse(response, pageable);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving audit logs by user %d: %s - %s", 
                    usuarioId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving audit logs by user " + usuarioId;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<AuditLogDTO> getRecentAuditLogs(int limit) {
        logger.debug("Retrieving {} recent audit logs", limit);

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_RECENT_ENDPOINT)
                    .queryParam("limit", limit)
                    .toUriString();

            List<AuditLogDTO> auditLogs = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<AuditLogDTO>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (auditLogs != null) {
                logger.debug("Retrieved {} recent audit logs", auditLogs.size());
                return auditLogs;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving recent audit logs: {}", e.getMessage());
            return List.of();
        }
    }
    @Override
    public Map<String, Object> getAuditStatistics(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        logger.debug("Retrieving audit statistics for date range: {} to {}", fechaInicio, fechaFin);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(AUDIT_STATISTICS_ENDPOINT);
            
            if (fechaInicio != null) {
                builder.queryParam("fechaInicio", fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            if (fechaFin != null) {
                builder.queryParam("fechaFin", fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            String uri = builder.toUriString();

            Map<String, Object> statistics = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (statistics != null) {
                logger.debug("Retrieved audit statistics");
                return statistics;
            } else {
                return new HashMap<>();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving audit statistics: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public List<String> getDistinctEntidades() {
        logger.debug("Retrieving distinct entidades");

        try {
            List<String> entidades = webClient.get()
                    .uri(AUDIT_ENTIDADES_ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (entidades != null) {
                logger.debug("Retrieved {} distinct entidades", entidades.size());
                return entidades;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving distinct entidades: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<String> getDistinctAcciones() {
        logger.debug("Retrieving distinct acciones");

        try {
            List<String> acciones = webClient.get()
                    .uri(AUDIT_ACCIONES_ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (acciones != null) {
                logger.debug("Retrieved {} distinct acciones", acciones.size());
                return acciones;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving distinct acciones: {}", e.getMessage());
            return List.of();
        }
    }
    @Override
    public boolean auditLogExists(Long logId) {
        if (logId == null) {
            return false;
        }

        try {
            webClient.head()
                    .uri(AUDIT_LOG_BY_ID_ENDPOINT, logId)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.debug("Audit log {} exists in backend", logId);
            return true;
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Audit log {} does not exist", logId);
                return false;
            }
            
            // For other errors, assume it exists to be safe
            logger.warn("Error checking audit log existence for {}: {}", logId, e.getMessage());
            return true;
        } catch (Exception e) {
            logger.warn("Error checking audit log existence for {}: {}", logId, e.getMessage());
            return true; // Assume exists on error to be safe
        }
    }

    @Override
    public String exportAuditLogsToCSV(String entidad, 
                                      String accion, 
                                      LocalDateTime fechaInicio, 
                                      LocalDateTime fechaFin) {
        logger.debug("Exporting audit logs to CSV: entidad={}, accion={}, fechaInicio={}, fechaFin={}", 
                    entidad, accion, fechaInicio, fechaFin);

        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath(AUDIT_LOGS_ENDPOINT + "/exportar/csv");
            
            if (entidad != null && !entidad.trim().isEmpty()) {
                builder.queryParam("entidad", entidad.trim().toUpperCase());
            }
            
            if (accion != null && !accion.trim().isEmpty()) {
                builder.queryParam("accion", accion.trim().toUpperCase());
            }
            
            if (fechaInicio != null) {
                builder.queryParam("fechaInicio", fechaInicio.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            if (fechaFin != null) {
                builder.queryParam("fechaFin", fechaFin.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }

            String uri = builder.toUriString();

            String csvContent = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (csvContent != null) {
                logger.info("Successfully exported audit logs to CSV");
                return csvContent;
            } else {
                return "";
            }
            
        } catch (Exception e) {
            logger.error("Error exporting audit logs to CSV: {}", e.getMessage());
            return "";
        }
    }

    @Override
    public String formatJsonChanges(String cambiosJson) {
        if (cambiosJson == null || cambiosJson.trim().isEmpty() || "{}".equals(cambiosJson.trim())) {
            return "Sin cambios registrados";
        }

        try {
            // Simple formatting for JSON display
            // In a real implementation, you might use Jackson ObjectMapper for better formatting
            return cambiosJson.replace("{", "{\n  ")
                             .replace("}", "\n}")
                             .replace(",", ",\n  ")
                             .replace(":", ": ");
        } catch (Exception e) {
            logger.warn("Error formatting JSON changes: {}", e.getMessage());
            return cambiosJson; // Return original if formatting fails
        }
    }
    @Override
    public int archiveOldAuditLogs(LocalDateTime fechaLimite) {
        logger.debug("Archiving old audit logs before date: {}", fechaLimite);
        
        if (fechaLimite == null) {
            throw new IllegalArgumentException("Fecha limite cannot be null");
        }

        try {
            String uri = UriComponentsBuilder.fromPath(AUDIT_LOGS_ENDPOINT + "/archivar")
                    .queryParam("fechaLimite", fechaLimite.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .toUriString();

            Integer archivedCount = webClient.post()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .retryWhen(retryConfiguration)
                    .block();

            int result = archivedCount != null ? archivedCount : 0;
            logger.info("Archived {} old audit logs", result);
            return result;
            
        } catch (Exception e) {
            logger.error("Error archiving old audit logs: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * Builds sort parameter string from Pageable.
     * Defaults to sorting by fechaHora in descending order (newest first).
     * 
     * @param pageable the pagination information
     * @return sort parameter string
     */
    private String buildSortParameter(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable.getSort().toString().replace(": ", ",");
        } else {
            // Default sorting: newest audit logs first
            return "fechaHora,desc";
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
    private Page<AuditLogDTO> mapPageResponse(Map<String, Object> response, Pageable pageable) {
        if (response == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        try {
            List<Map<String, Object>> content = (List<Map<String, Object>>) response.get("content");
            Number totalElements = (Number) response.get("totalElements");
            
            List<AuditLogDTO> auditLogs = content != null ? 
                content.stream().map(this::mapToAuditLogDTO).toList() : List.of();
            
            long total = totalElements != null ? totalElements.longValue() : 0L;
            
            return new PageImpl<>(auditLogs, pageable, total);
            
        } catch (Exception e) {
            logger.error("Error mapping page response: {}", e.getMessage());
            return new PageImpl<>(List.of(), pageable, 0);
        }
    }

    /**
     * Maps a Map to AuditLogDTO object.
     * 
     * @param map the map containing audit log data
     * @return mapped AuditLogDTO object
     */
    private AuditLogDTO mapToAuditLogDTO(Map<String, Object> map) {
        AuditLogDTO auditLog = new AuditLogDTO();
        
        try {
            if (map.get("logId") != null) {
                auditLog.setLogId(((Number) map.get("logId")).longValue());
            }
            auditLog.setEntidad((String) map.get("entidad"));
            if (map.get("entidadId") != null) {
                auditLog.setEntidadId(((Number) map.get("entidadId")).longValue());
            }
            auditLog.setAccion((String) map.get("accion"));
            if (map.get("usuarioId") != null) {
                auditLog.setUsuarioId(((Number) map.get("usuarioId")).longValue());
            }
            auditLog.setUsuarioEmail((String) map.get("usuarioEmail"));
            auditLog.setUsuarioNombre((String) map.get("usuarioNombre"));
            
            // Parse fechaHora
            if (map.get("fechaHora") != null) {
                String fechaHoraStr = (String) map.get("fechaHora");
                auditLog.setFechaHora(LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            auditLog.setDireccionIp((String) map.get("direccionIp"));
            auditLog.setUserAgent((String) map.get("userAgent"));
            auditLog.setEstadoAnterior((String) map.get("estadoAnterior"));
            auditLog.setEstadoPosterior((String) map.get("estadoPosterior"));
            auditLog.setCambios((String) map.get("cambios"));
            auditLog.setDetalles((String) map.get("detalles"));
            if (map.get("negocioId") != null) {
                auditLog.setNegocioId(((Number) map.get("negocioId")).longValue());
            }
            auditLog.setSessionId((String) map.get("sessionId"));
            auditLog.setSistemaGenerado((Boolean) map.get("sistemaGenerado"));
            
        } catch (Exception e) {
            logger.warn("Error mapping audit log data: {}", e.getMessage());
        }
        
        return auditLog;
    }
}