package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.RoleDTO;
import java.util.List;

/**
 * Service interface for managing roles.
 * Provides CRUD operations for roles with caching support and duplicate checking.
 * Handles communication with backend API at localhost:8080.
 * 
 * Features:
 * - Complete CRUD operations (getAllRoles, getRoleById, createRole, updateRole, deleteRole)
 * - Duplicate name checking with 409 Conflict handling
 * - System role protection (ADMIN, EMPLEADO roles cannot be deleted)
 * - Caching layer for read operations
 * - Cache invalidation on write operations (create, update, delete)
 * - Error handling for 409 Conflict (duplicate names) and 404 Not Found
 * - WebClient integration for REST API communication
 * 
 * Requirements: 9.1-9.6, 18.5-18.6
 */
public interface RoleService {

    /**
     * Retrieve all roles from the backend API.
     * Uses caching with configurable TTL. If cached data is available and not expired,
     * returns cached results. Otherwise, fetches from backend and updates cache.
     * 
     * @return List of all RoleDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<RoleDTO> getAllRoles();

    /**
     * Retrieve a specific role by its ID.
     * First checks cache, then queries backend API if not found or expired.
     * 
     * @param id the role ID
     * @return RoleDTO for the specified ID
     * @throws EntityNotFoundException if role with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    RoleDTO getRoleById(Long id);

    /**
     * Create a new role in the backend.
     * Validates the roleDTO, checks for duplicate names, and sends to backend API.
     * Invalidates cache on success.
     * 
     * @param roleDTO the role data to create (rolId should be null)
     * @return RoleDTO with the created role including generated ID and timestamps
     * @throws ConflictException if role name already exists (409)
     * @throws ValidationException if roleDTO validation fails (400)
     * @throws RuntimeException if backend API call fails
     */
    RoleDTO createRole(RoleDTO roleDTO);

    /**
     * Update an existing role in the backend.
     * Validates the roleDTO, checks for duplicate names, and sends update to backend API.
     * System roles have limited modification capabilities.
     * Invalidates cache on success.
     * 
     * @param id the ID of the role to update
     * @param roleDTO the updated role data
     * @return RoleDTO with the updated role data
     * @throws EntityNotFoundException if role with the given ID does not exist (404)
     * @throws ConflictException if updated name conflicts with existing role (409)
     * @throws BusinessRuleException if trying to modify system role inappropriately (422)
     * @throws ValidationException if roleDTO validation fails (400)
     * @throws RuntimeException if backend API call fails
     */
    RoleDTO updateRole(Long id, RoleDTO roleDTO);

    /**
     * Delete a role from the backend.
     * System roles (ADMIN, EMPLEADO) cannot be deleted.
     * Attempts to delete the role and invalidates cache on success.
     * 
     * @param id the ID of the role to delete
     * @throws EntityNotFoundException if role with the given ID does not exist (404)
     * @throws BusinessRuleException if role cannot be deleted due to business rules (422)
     *         e.g., role is a system role, or role has associated users
     * @throws RuntimeException if backend API call fails
     */
    void deleteRole(Long id);

    /**
     * Check if a role exists by ID.
     * Optimized method that checks cache first, then makes a lightweight backend call.
     * 
     * @param id the role ID to check
     * @return true if the role exists, false otherwise
     */
    boolean roleExists(Long id);

    /**
     * Check if a role name already exists.
     * Used for duplicate validation before creating or updating roles.
     * 
     * @param nombre the role name to check
     * @param excludeId the role ID to exclude from check (for updates)
     * @return true if the name exists for a different role, false otherwise
     */
    boolean roleNameExists(String nombre, Long excludeId);

    /**
     * Get roles by active status.
     * Retrieves only active or inactive roles based on the parameter.
     * 
     * @param activo true for active roles, false for inactive roles
     * @return List of RoleDTO objects matching the active status
     * @throws RuntimeException if backend API call fails
     */
    List<RoleDTO> getRolesByActiveStatus(boolean activo);

    /**
     * Get system roles (non-deletable roles like ADMIN, EMPLEADO).
     * These roles are essential for system operation and cannot be deleted.
     * 
     * @return List of system RoleDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<RoleDTO> getSystemRoles();

    /**
     * Get custom user-defined roles.
     * These roles can be modified and deleted by administrators.
     * 
     * @return List of custom RoleDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<RoleDTO> getCustomRoles();

    /**
     * Refresh the roles cache.
     * Forces a reload of all roles from backend and updates cache.
     * Useful when cache may be stale or after bulk operations.
     * 
     * @return List of refreshed RoleDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<RoleDTO> refreshRolesCache();

    /**
     * Clear all roles from cache.
     * Invalidates all cached role data, forcing fresh retrieval on next access.
     */
    void clearCache();

    /**
     * Get cache statistics for roles.
     * Provides information about cache usage, hit ratios, and configuration.
     * 
     * @return Map containing cache statistics (size, hits, misses, TTL, etc.)
     */
    java.util.Map<String, Object> getCacheStatistics();
}