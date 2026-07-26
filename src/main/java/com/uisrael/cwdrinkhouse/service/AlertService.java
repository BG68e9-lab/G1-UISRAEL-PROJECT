package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.AlertDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface for managing alerts.
 * Provides alert management with filtering by type and read status.
 * Handles communication with backend API at localhost:8080.
 * 
 * Features:
 * - Alert filtering by type (STOCK_BAJO, VENCIMIENTO_PROXIMO, SISTEMA, USUARIO, NEGOCIO)
 * - Mark as read/unread functionality
 * - Priority-based sorting (ALTA, MEDIA, BAJA)
 * - Pagination support for large alert lists
 * - Unread alerts counting
 * - Auto-expiration handling
 * - WebClient integration for REST API communication
 * 
 * Requirements: 10.1-10.12, 18.5-18.6
 */
public interface AlertService {

    /**
     * Retrieve all alerts with pagination.
     * Returns paginated results sorted by priority (ALTA first) and creation date (newest first).
     * 
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAllAlerts(Pageable pageable);

    /**
     * Retrieve alerts filtered by type with pagination.
     * Valid types: STOCK_BAJO, VENCIMIENTO_PROXIMO, SISTEMA, USUARIO, NEGOCIO
     * 
     * @param tipo the alert type filter
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects matching the type
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAlertsByType(String tipo, Pageable pageable);

    /**
     * Retrieve alerts filtered by read status with pagination.
     * 
     * @param leido true for read alerts, false for unread alerts
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects matching the read status
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAlertsByReadStatus(boolean leido, Pageable pageable);

    /**
     * Retrieve alerts filtered by type and read status with pagination.
     * Combines type and read status filters for precise filtering.
     * 
     * @param tipo the alert type filter (can be null for no type filter)
     * @param leido true for read alerts, false for unread alerts, null for all
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects matching the filters
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAlertsByTypeAndReadStatus(String tipo, Boolean leido, Pageable pageable);

    /**
     * Retrieve a specific alert by its ID.
     * 
     * @param id the alert ID
     * @return AlertDTO for the specified ID
     * @throws EntityNotFoundException if alert with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    AlertDTO getAlertById(Long id);

    /**
     * Create a new alert in the backend.
     * Validates the alertDTO and sends to backend API.
     * 
     * @param alertDTO the alert data to create (alertaId should be null)
     * @return AlertDTO with the created alert including generated ID and timestamps
     * @throws ValidationException if alertDTO validation fails (400)
     * @throws RuntimeException if backend API call fails
     */
    AlertDTO createAlert(AlertDTO alertDTO);

    /**
     * Mark an alert as read by a user.
     * Updates the alert's read status, read timestamp, and user who read it.
     * 
     * @param id the alert ID to mark as read
     * @param usuarioEmail the email of user who read the alert
     * @return AlertDTO with updated read information
     * @throws EntityNotFoundException if alert with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    AlertDTO markAlertAsRead(Long id, String usuarioEmail);

    /**
     * Mark an alert as unread.
     * Resets the alert's read status, read timestamp, and user information.
     * 
     * @param id the alert ID to mark as unread
     * @return AlertDTO with reset read information
     * @throws EntityNotFoundException if alert with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    AlertDTO markAlertAsUnread(Long id);

    /**
     * Mark multiple alerts as read by a user.
     * Bulk operation for marking multiple alerts as read.
     * 
     * @param alertIds list of alert IDs to mark as read
     * @param usuarioEmail the email of user who read the alerts
     * @return List of AlertDTO objects with updated read information
     * @throws RuntimeException if backend API call fails
     */
    List<AlertDTO> markAlertsAsRead(List<Long> alertIds, String usuarioEmail);

    /**
     * Delete an alert from the backend.
     * Removes the alert permanently from the system.
     * 
     * @param id the ID of the alert to delete
     * @throws EntityNotFoundException if alert with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    void deleteAlert(Long id);

    /**
     * Count unread alerts.
     * Returns the total number of unread alerts in the system.
     * Used for dashboard badges and notifications.
     * 
     * @return number of unread alerts
     * @throws RuntimeException if backend API call fails
     */
    long countUnreadAlerts();

    /**
     * Count unread alerts by type.
     * Returns the number of unread alerts for a specific type.
     * 
     * @param tipo the alert type to count
     * @return number of unread alerts of the specified type
     * @throws RuntimeException if backend API call fails
     */
    long countUnreadAlertsByType(String tipo);

    /**
     * Get unread alerts for dashboard.
     * Returns a limited number of most recent unread alerts for dashboard display.
     * Sorted by priority (ALTA first) and creation date (newest first).
     * 
     * @param limit maximum number of alerts to return
     * @return List of most recent unread AlertDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<AlertDTO> getUnreadAlertsForDashboard(int limit);

    /**
     * Get alerts by priority.
     * Retrieves alerts filtered by priority level.
     * 
     * @param prioridad the priority level (ALTA, MEDIA, BAJA)
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects with the specified priority
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAlertsByPriority(String prioridad, Pageable pageable);

    /**
     * Get alerts for a specific entity.
     * Retrieves alerts related to a specific entity (e.g., product, lot, user).
     * 
     * @param entidadTipo the entity type (e.g., PRODUCTO, LOTE, USUARIO)
     * @param entidadId the entity ID
     * @param pageable pagination parameters
     * @return Page of AlertDTO objects related to the entity
     * @throws RuntimeException if backend API call fails
     */
    Page<AlertDTO> getAlertsByEntity(String entidadTipo, Long entidadId, Pageable pageable);

    /**
     * Clean expired alerts.
     * Removes alerts that have passed their expiration date.
     * This is typically called by a scheduled job.
     * 
     * @return number of expired alerts removed
     * @throws RuntimeException if backend API call fails
     */
    int cleanExpiredAlerts();

    /**
     * Check if an alert exists by ID.
     * Optimized method for checking alert existence.
     * 
     * @param id the alert ID to check
     * @return true if the alert exists, false otherwise
     */
    boolean alertExists(Long id);
}