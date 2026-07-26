package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.RoleDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.CacheManager;
import com.uisrael.cwdrinkhouse.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of RoleService using WebClient for backend API communication.
 * Provides complete CRUD operations for roles with caching, duplicate checking, and error handling.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Caching layer using CacheManager with configurable TTL
 * - Cache invalidation on write operations (create, update, delete)
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Duplicate role name validation with 409 Conflict handling
 * - System role protection (prevents deletion of ADMIN, EMPLEADO roles)
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 9.1-9.6, 18.5-18.6
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    
    private static final String ROLES_ENDPOINT = "/api/v1/roles";
    private static final String ROLE_BY_ID_ENDPOINT = "/api/v1/roles/{id}";
    private static final String ROLES_BY_ACTIVE_ENDPOINT = "/api/v1/roles/active/{activo}";
    private static final String ROLES_SYSTEM_ENDPOINT = "/api/v1/roles/system";
    private static final String ROLES_CUSTOM_ENDPOINT = "/api/v1/roles/custom";
    private static final String ROLE_EXISTS_ENDPOINT = "/api/v1/roles/{id}/exists";
    private static final String ROLE_NAME_EXISTS_ENDPOINT = "/api/v1/roles/exists/name";
    
    private static final String ROLES_CACHE_KEY = "roles:all";
    private static final String ROLE_CACHE_KEY_PREFIX = "role:";
    private static final String ACTIVE_ROLES_CACHE_KEY_PREFIX = "roles:active:";
    private static final String SYSTEM_ROLES_CACHE_KEY = "roles:system";
    private static final String CUSTOM_ROLES_CACHE_KEY = "roles:custom";

    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final Retry retryConfiguration;

    @Autowired
    public RoleServiceImpl(WebClient webClient, CacheManager cacheManager, Retry retryConfiguration) {
        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.retryConfiguration = retryConfiguration;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        logger.debug("Retrieving all roles");
        
        // Try to get from cache first
        List<RoleDTO> cachedRoles = cacheManager.getRole(ROLES_CACHE_KEY);
        if (cachedRoles != null) {
            logger.debug("Retrieved {} roles from cache", cachedRoles.size());
            return cachedRoles;
        }

        // Not in cache, fetch from backend
        try {
            // First try to get as paginated response
            List<RoleDTO> roles;
            try {
                RolePageResponse pageResponse = webClient.get()
                        .uri(ROLES_ENDPOINT)
                        .retrieve()
                        .bodyToMono(RolePageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();
                
                roles = (pageResponse != null) ? pageResponse.getContent() : null;
                logger.debug("Retrieved paginated response with {} roles", 
                        (roles != null) ? roles.size() : 0);
                
            } catch (Exception e) {
                // If paginated fails, try as direct array
                logger.debug("Paginated fetch failed, trying direct array: {}", e.getMessage());
                roles = webClient.get()
                        .uri(ROLES_ENDPOINT)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<RoleDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();
            }

            if (roles != null) {
                // Cache the result
                cacheManager.putRole(ROLES_CACHE_KEY, roles);
                
                // Also cache individual roles
                roles.forEach(role -> 
                    cacheManager.putRole(ROLE_CACHE_KEY_PREFIX + role.getRolId(), role)
                );
                
                logger.info("Retrieved and cached {} roles from backend", roles.size());
                return roles;
            } else {
                logger.warn("Backend returned null for roles list");
                return List.of(); // Return empty list instead of null
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving roles: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving roles from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public RoleDTO getRoleById(Long id) {
        logger.debug("Retrieving role with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }

        String cacheKey = ROLE_CACHE_KEY_PREFIX + id;
        
        // Try to get from cache first
        RoleDTO cachedRole = cacheManager.getRole(cacheKey);
        if (cachedRole != null) {
            logger.debug("Retrieved role {} from cache", id);
            return cachedRole;
        }

        // Not in cache, fetch from backend
        try {
            RoleDTO role = webClient.get()
                    .uri(ROLE_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(RoleDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (role != null) {
                // Cache the result
                cacheManager.putRole(cacheKey, role);
                logger.debug("Retrieved and cached role {} from backend", id);
                return role;
            } else {
                throw new EntityNotFoundException("Role not found with ID: " + id);
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Role not found with ID: " + id);
            } else {
                String errorMsg = String.format("Backend API error while retrieving role %d: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while retrieving role " + id + " from backend";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        logger.debug("Creating new role: {}", roleDTO.getNombre());
        
        if (roleDTO == null) {
            throw new IllegalArgumentException("RoleDTO cannot be null");
        }

        if (roleDTO.getRolId() != null) {
            throw new IllegalArgumentException("Role ID must be null for creation");
        }

        try {
            RoleDTO createdRole = webClient.post()
                    .uri(ROLES_ENDPOINT)
                    .bodyValue(roleDTO)
                    .retrieve()
                    .bodyToMono(RoleDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdRole != null) {
                // Invalidate cache to force refresh on next read
                invalidateRolesCache();
                logger.info("Successfully created role: {} with ID: {}", 
                           createdRole.getNombre(), createdRole.getRolId());
                return createdRole;
            } else {
                throw new RuntimeException("Backend returned null after role creation");
            }
            
        } catch (WebClientResponseException e) {
            handleWriteOperationError(e, "creation", roleDTO.getNombre());
            return null; // This line will never be reached due to exception handling
        } catch (Exception e) {
            String errorMsg = "Unexpected error while creating role: " + roleDTO.getNombre();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        logger.debug("Updating role with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }
        
        if (roleDTO == null) {
            throw new IllegalArgumentException("RoleDTO cannot be null");
        }

        // Set the ID to ensure consistency
        roleDTO.setRolId(id);

        try {
            RoleDTO updatedRole = webClient.put()
                    .uri(ROLE_BY_ID_ENDPOINT, id)
                    .bodyValue(roleDTO)
                    .retrieve()
                    .bodyToMono(RoleDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedRole != null) {
                // Invalidate cache to force refresh on next read
                invalidateRolesCache();
                logger.info("Successfully updated role: {} with ID: {}", 
                           updatedRole.getNombre(), updatedRole.getRolId());
                return updatedRole;
            } else {
                throw new RuntimeException("Backend returned null after role update");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Role not found with ID: " + id);
            } else {
                handleWriteOperationError(e, "update", roleDTO.getNombre());
                return null; // This line will never be reached due to exception handling
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while updating role " + id;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void deleteRole(Long id) {
        logger.debug("Deleting role with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Role ID cannot be null");
        }

        try {
            webClient.delete()
                    .uri(ROLE_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            // Invalidate cache to force refresh on next read
            invalidateRolesCache();
            logger.info("Successfully deleted role with ID: {}", id);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                throw new EntityNotFoundException("Role not found with ID: " + id);
            } else if (e.getStatusCode().value() == 422) {
                throw new BusinessRuleException("Role cannot be deleted: " + e.getResponseBodyAsString());
            } else {
                String errorMsg = String.format("Backend API error while deleting role %d: %s - %s", 
                        id, e.getStatusCode(), e.getResponseBodyAsString());
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
            }
        } catch (Exception e) {
            String errorMsg = "Unexpected error while deleting role " + id;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean roleExists(Long id) {
        if (id == null) {
            return false;
        }

        // Check cache first
        String cacheKey = ROLE_CACHE_KEY_PREFIX + id;
        if (cacheManager.getRole(cacheKey) != null) {
            return true;
        }

        try {
            Boolean exists = webClient.get()
                    .uri(ROLE_EXISTS_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(exists);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                return false;
            }
            logger.error("Error checking role existence for ID {}: {}", id, e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error checking role existence for ID {}: {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean roleNameExists(String nombre, Long excludeId) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("nombre", nombre.trim());
            if (excludeId != null) {
                requestBody.put("excludeId", excludeId);
            }

            Boolean exists = webClient.post()
                    .uri(ROLE_NAME_EXISTS_ENDPOINT)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(exists);
            
        } catch (Exception e) {
            logger.error("Error checking role name existence for '{}': {}", nombre, e.getMessage());
            return false;
        }
    }

    @Override
    public List<RoleDTO> getRolesByActiveStatus(boolean activo) {
        logger.debug("Retrieving roles with active status: {}", activo);
        
        String cacheKey = ACTIVE_ROLES_CACHE_KEY_PREFIX + activo;
        
        // Try to get from cache first
        List<RoleDTO> cachedRoles = cacheManager.getRole(cacheKey);
        if (cachedRoles != null) {
            logger.debug("Retrieved {} active={} roles from cache", cachedRoles.size(), activo);
            return cachedRoles;
        }

        try {
            // First try to get as paginated response
            List<RoleDTO> roles;
            try {
                RolePageResponse pageResponse = webClient.get()
                        .uri(ROLES_BY_ACTIVE_ENDPOINT, activo)
                        .retrieve()
                        .bodyToMono(RolePageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();
                
                roles = (pageResponse != null) ? pageResponse.getContent() : null;
                
            } catch (Exception e) {
                // If paginated fails, try as direct array
                logger.debug("Paginated fetch failed, trying direct array: {}", e.getMessage());
                roles = webClient.get()
                        .uri(ROLES_BY_ACTIVE_ENDPOINT, activo)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<RoleDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();
            }

            if (roles != null) {
                // Cache the result
                cacheManager.putRole(cacheKey, roles);
                logger.debug("Retrieved and cached {} active={} roles from backend", roles.size(), activo);
                return roles;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            String errorMsg = "Error retrieving roles by active status: " + activo;
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<RoleDTO> getSystemRoles() {
        logger.debug("Retrieving system roles");
        
        // Try to get from cache first
        List<RoleDTO> cachedRoles = cacheManager.getRole(SYSTEM_ROLES_CACHE_KEY);
        if (cachedRoles != null) {
            logger.debug("Retrieved {} system roles from cache", cachedRoles.size());
            return cachedRoles;
        }

        try {
            // First try to get as paginated response
            List<RoleDTO> roles;
            try {
                RolePageResponse pageResponse = webClient.get()
                        .uri(ROLES_SYSTEM_ENDPOINT)
                        .retrieve()
                        .bodyToMono(RolePageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();
                
                roles = (pageResponse != null) ? pageResponse.getContent() : null;
                
            } catch (Exception e) {
                // If paginated fails, try as direct array
                logger.debug("Paginated fetch failed, trying direct array: {}", e.getMessage());
                roles = webClient.get()
                        .uri(ROLES_SYSTEM_ENDPOINT)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<RoleDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();
            }

            if (roles != null) {
                // Cache the result
                cacheManager.putRole(SYSTEM_ROLES_CACHE_KEY, roles);
                logger.debug("Retrieved and cached {} system roles from backend", roles.size());
                return roles;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            String errorMsg = "Error retrieving system roles";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<RoleDTO> getCustomRoles() {
        logger.debug("Retrieving custom roles");
        
        // Try to get from cache first
        List<RoleDTO> cachedRoles = cacheManager.getRole(CUSTOM_ROLES_CACHE_KEY);
        if (cachedRoles != null) {
            logger.debug("Retrieved {} custom roles from cache", cachedRoles.size());
            return cachedRoles;
        }

        try {
            // First try to get as paginated response
            List<RoleDTO> roles;
            try {
                RolePageResponse pageResponse = webClient.get()
                        .uri(ROLES_CUSTOM_ENDPOINT)
                        .retrieve()
                        .bodyToMono(RolePageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();
                
                roles = (pageResponse != null) ? pageResponse.getContent() : null;
                
            } catch (Exception e) {
                // If paginated fails, try as direct array
                logger.debug("Paginated fetch failed, trying direct array: {}", e.getMessage());
                roles = webClient.get()
                        .uri(ROLES_CUSTOM_ENDPOINT)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<RoleDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();
            }

            if (roles != null) {
                // Cache the result
                cacheManager.putRole(CUSTOM_ROLES_CACHE_KEY, roles);
                logger.debug("Retrieved and cached {} custom roles from backend", roles.size());
                return roles;
            } else {
                return List.of();
            }
            
        } catch (Exception e) {
            String errorMsg = "Error retrieving custom roles";
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<RoleDTO> refreshRolesCache() {
        logger.debug("Refreshing roles cache");
        
        // Clear existing cache
        clearCache();
        
        // Fetch fresh data
        return getAllRoles();
    }

    @Override
    public void clearCache() {
        logger.debug("Clearing roles cache");
        cacheManager.invalidateRoles();
    }

    @Override
    public Map<String, Object> getCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRolesCacheSize", cacheManager.rolesCacheSize());
        stats.put("overallCacheSize", cacheManager.size());
        stats.putAll(cacheManager.getStatistics());
        return stats;
    }

    /**
     * Invalidates all role-related cache entries.
     */
    private void invalidateRolesCache() {
        cacheManager.invalidateRoles();
        logger.debug("Invalidated roles cache due to write operation");
    }

    /**
     * Handles errors from write operations (create, update, delete) and maps them to appropriate exceptions.
     * 
     * @param e the WebClientResponseException from the backend
     * @param operation the operation being performed (for logging)
     * @param roleName the role name (for logging)
     */
    private void handleWriteOperationError(WebClientResponseException e, String operation, String roleName) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        
        logger.error("Role {} operation failed for '{}': {} - {}", 
                    operation, roleName, statusCode, responseBody);

        switch (statusCode) {
            case 400:
                throw new ValidationException("Invalid role data: " + responseBody);
            case 409:
                throw new ConflictException("Role name already exists: " + roleName);
            case 422:
                throw new BusinessRuleException("Business rule violation: " + responseBody);
            default:
                throw new RuntimeException(String.format(
                    "Backend API error during role %s: %d - %s", operation, statusCode, responseBody));
        }
    }

    /**
     * Response wrapper for paginated role data from backend.
     */
    private static class RolePageResponse {
        private List<RoleDTO> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        // Getters and setters
        public List<RoleDTO> getContent() { return content; }
        public void setContent(List<RoleDTO> content) { this.content = content; }
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}