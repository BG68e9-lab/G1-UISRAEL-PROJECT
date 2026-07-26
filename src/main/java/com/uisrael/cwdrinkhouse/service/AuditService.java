package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.AuditLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing audit logs.
 * Provides comprehensive audit log retrieval capabilities with filtering options
 * and detailed change tracking for system transparency and compliance.
 * Handles communication with backend API at localhost:8080.
 * 
 * Features:
 * - Complete audit log retrieval with pagination support
 * - Advanced filtering by entidad, accion, and date range
 * - Detailed audit log information with JSON change display
 * - User-based audit trail tracking
 * - Entity-specific audit history
 * - WebClient integration for REST API communication
 * - Comprehensive error handling and logging
 * 
 * Requirements: 11.1-11.7, 18.5-18.6
 */
public interface AuditService {

    /**
     * Retrieve all audit logs with pagination support.
     * Returns audit logs sorted by fechaHora in descending order (newest first).
     * 
     * @param pageable pagination information (page, size, sort)
     * @return Page of AuditLogDTO objects with pagination metadata
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAllAuditLogs(Pageable pageable);

    /**
     * Retrieve audit logs filtered by entity type.
     * Filters audit logs to show only entries for the specified entity type.
     * 
     * @param entidad the entity type to filter by (e.g., CATEGORIA, PRODUCTO, USUARIO)
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects for the specified entity type
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsByEntidad(String entidad, Pageable pageable);

    /**
     * Retrieve audit logs filtered by action type.
     * Filters audit logs to show only entries for the specified action.
     * 
     * @param accion the action type to filter by (CREAR, ACTUALIZAR, ELIMINAR, LEER)
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects for the specified action
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsByAccion(String accion, Pageable pageable);

    /**
     * Retrieve audit logs filtered by date range.
     * Returns audit logs that occurred between the specified start and end dates.
     * 
     * @param fechaInicio the start date (inclusive)
     * @param fechaFin the end date (inclusive)
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects within the date range
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsByDateRange(LocalDateTime fechaInicio, 
                                             LocalDateTime fechaFin, 
                                             Pageable pageable);

    /**
     * Retrieve audit logs with combined filtering.
     * Supports filtering by entity type, action, and date range simultaneously.
     * Null parameters are ignored in the filter.
     * 
     * @param entidad the entity type filter (nullable)
     * @param accion the action type filter (nullable)
     * @param fechaInicio the start date filter (nullable)
     * @param fechaFin the end date filter (nullable)
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects matching all specified filters
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsFiltered(String entidad, 
                                          String accion, 
                                          LocalDateTime fechaInicio, 
                                          LocalDateTime fechaFin, 
                                          Pageable pageable);

    /**
     * Retrieve a specific audit log entry by its ID.
     * Returns detailed audit log information including formatted JSON changes.
     * 
     * @param logId the audit log ID
     * @return AuditLogDTO with complete audit information
     * @throws EntityNotFoundException if audit log with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    AuditLogDTO getAuditLogDetail(Long logId);

    /**
     * Retrieve audit logs for a specific entity instance.
     * Returns the complete audit trail for a particular entity.
     * 
     * @param entidad the entity type (e.g., CATEGORIA, PRODUCTO)
     * @param entidadId the specific entity ID
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects for the specified entity instance
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsByEntity(String entidad, Long entidadId, Pageable pageable);

    /**
     * Retrieve audit logs for a specific user.
     * Returns all audit entries created by the specified user.
     * 
     * @param usuarioId the user ID
     * @param pageable pagination information
     * @return Page of AuditLogDTO objects for the specified user
     * @throws RuntimeException if backend API call fails
     */
    Page<AuditLogDTO> getAuditLogsByUser(Long usuarioId, Pageable pageable);

    /**
     * Retrieve recent audit logs for dashboard display.
     * Returns the most recent audit activities for system monitoring.
     * 
     * @param limit maximum number of recent logs to retrieve
     * @return List of recent AuditLogDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<AuditLogDTO> getRecentAuditLogs(int limit);

    /**
     * Get audit statistics for a date range.
     * Provides aggregated information about audit activities.
     * 
     * @param fechaInicio the start date for statistics
     * @param fechaFin the end date for statistics
     * @return Map containing audit statistics (counts by action, entity, etc.)
     * @throws RuntimeException if backend API call fails
     */
    Map<String, Object> getAuditStatistics(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Get distinct entity types that have audit logs.
     * Useful for populating filter dropdowns in the UI.
     * 
     * @return List of distinct entity type strings
     * @throws RuntimeException if backend API call fails
     */
    List<String> getDistinctEntidades();

    /**
     * Get distinct actions that have audit logs.
     * Useful for populating filter dropdowns in the UI.
     * 
     * @return List of distinct action strings
     * @throws RuntimeException if backend API call fails
     */
    List<String> getDistinctAcciones();

    /**
     * Check if an audit log exists by ID.
     * Optimized method for existence checking without full retrieval.
     * 
     * @param logId the audit log ID to check
     * @return true if the audit log exists, false otherwise
     */
    boolean auditLogExists(Long logId);

    /**
     * Export audit logs to CSV format.
     * Returns audit logs in CSV format for external analysis or compliance reporting.
     * 
     * @param entidad optional entity filter
     * @param accion optional action filter
     * @param fechaInicio optional start date filter
     * @param fechaFin optional end date filter
     * @return CSV content as string
     * @throws RuntimeException if backend API call fails
     */
    String exportAuditLogsToCSV(String entidad, 
                               String accion, 
                               LocalDateTime fechaInicio, 
                               LocalDateTime fechaFin);

    /**
     * Get formatted JSON changes for display.
     * Takes raw JSON changes and formats them for user-friendly display.
     * 
     * @param cambiosJson the raw JSON changes string
     * @return formatted and readable JSON changes
     */
    String formatJsonChanges(String cambiosJson);

    /**
     * Archive old audit logs.
     * Moves audit logs older than the specified date to archive storage.
     * 
     * @param fechaLimite audit logs older than this date will be archived
     * @return number of audit logs archived
     * @throws RuntimeException if backend API call fails
     */
    int archiveOldAuditLogs(LocalDateTime fechaLimite);
}