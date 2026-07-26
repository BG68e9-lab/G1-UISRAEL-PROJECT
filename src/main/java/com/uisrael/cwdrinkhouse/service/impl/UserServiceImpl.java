package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.UserDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of UserService using WebClient for backend API communication.
 * Provides complete user management with authentication and role management.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Complete CRUD operations for users
 * - Authentication method validation (LOCAL vs SSO)
 * - User status transitions with validation
 * - Role assignment and management
 * - Email duplicate detection
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 1.2-1.3, 8.1-8.14, 16.1, 18.5-18.6
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private static final String USERS_ENDPOINT = "/api/v1/usuarios";
    private static final String USER_BY_ID_ENDPOINT = "/api/v1/usuarios/{id}";
    private static final String USER_AUTHENTICATE_ENDPOINT = "/api/v1/usuarios/authenticate";
    private static final String USER_STATUS_ENDPOINT = "/api/v1/usuarios/{id}/status";
    private static final String USER_ROLES_ENDPOINT = "/api/v1/usuarios/{id}/roles";
    private static final String USER_SEARCH_ENDPOINT = "/api/v1/usuarios/search";
    private static final String USER_BY_ROLE_ENDPOINT = "/api/v1/usuarios/by-role/{roleName}";
    private static final String USER_EMAIL_CHECK_ENDPOINT = "/api/v1/usuarios/check-email";
    private static final String USER_COUNT_BY_STATUS_ENDPOINT = "/api/v1/usuarios/count-by-status";
    private static final String USER_RECENT_ENDPOINT = "/api/v1/usuarios/recent/{days}";
    private static final String USER_PENDING_ENDPOINT = "/api/v1/usuarios/pending";

    private final WebClient webClient;
    private final Retry retryConfiguration;

    @Autowired
    public UserServiceImpl(WebClient webClient, Retry webClientRetry) {
        this.webClient = webClient;
        this.retryConfiguration = webClientRetry;
    }

    @Override
    public Page<UserDTO> getAllUsers(int page, int size) {
        logger.debug("Retrieving users - page: {}, size: {}", page, size);
        
        try {
            String uri = USERS_ENDPOINT + "?page=" + page + "&size=" + size;
            UserPageResponse response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(UserPageResponse.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (response != null) {
                logger.info("Retrieved {} users from backend (page {}, size {})", 
                           response.getContent().size(), page, size);
                return new PageImpl<>(
                    response.getContent(), 
                    PageRequest.of(page, size), 
                    response.getTotalElements()
                );
            } else {
                logger.warn("Backend returned null for users list");
                return Page.empty();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving users: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving users from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<UserDTO> searchUsers(String nombres, String apellidos, String email, 
                                   String estadoCuenta, int page, int size) {
        logger.debug("Searching users with criteria - nombres: {}, apellidos: {}, email: {}, estado: {}", 
                    nombres, apellidos, email, estadoCuenta);
        
        try {
            StringBuilder uri = new StringBuilder(USER_SEARCH_ENDPOINT + "?page=" + page + "&size=" + size);
            
            if (nombres != null && !nombres.trim().isEmpty()) {
                uri.append("&nombres=").append(nombres.trim());
            }
            if (apellidos != null && !apellidos.trim().isEmpty()) {
                uri.append("&apellidos=").append(apellidos.trim());
            }
            if (email != null && !email.trim().isEmpty()) {
                uri.append("&email=").append(email.trim());
            }
            if (estadoCuenta != null && !estadoCuenta.trim().isEmpty()) {
                uri.append("&estadoCuenta=").append(estadoCuenta.trim());
            }

            UserPageResponse response = webClient.get()
                    .uri(uri.toString())
                    .retrieve()
                    .bodyToMono(UserPageResponse.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (response != null) {
                logger.info("Found {} users matching search criteria", response.getContent().size());
                return new PageImpl<>(
                    response.getContent(), 
                    PageRequest.of(page, size), 
                    response.getTotalElements()
                );
            } else {
                logger.warn("Backend returned null for user search results");
                return Page.empty();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while searching users: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error searching users from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO getUserById(Long id) {
        logger.debug("Retrieving user with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            UserDTO user = webClient.get()
                    .uri(USER_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (user != null) {
                logger.info("Retrieved user {}", id);
                return user;
            } else {
                throw new EntityNotFoundException("Usuario", id.toString());
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found in backend", id);
                throw new EntityNotFoundException("Usuario", id.toString());
            }
            
            String errorMsg = String.format("Backend API error while retrieving user %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error retrieving user " + id + " from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        logger.debug("Creating new user: {}", userDTO.getEmail());
        
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        
        // Validate authentication method requirements
        if (!validateAuthenticationMethod(userDTO)) {
            String error = userDTO.getAuthenticationValidationError();
            throw new ValidationException(error != null ? error : "Datos de autenticación inválidos");
        }
        
        // Ensure ID is null for creation
        userDTO.setUsuarioId(null);
        
        // Set default status if not provided
        if (userDTO.getEstadoCuenta() == null || userDTO.getEstadoCuenta().trim().isEmpty()) {
            userDTO.setEstadoCuenta("PENDIENTE");
        }

        try {
            UserDTO createdUser = webClient.post()
                    .uri(USERS_ENDPOINT)
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdUser != null) {
                logger.info("Successfully created user with ID: {} (email: {})", 
                           createdUser.getUsuarioId(), createdUser.getEmail());
                return createdUser;
            } else {
                throw new RuntimeException("Backend returned null after user creation");
            }
            
        } catch (WebClientResponseException e) {
            handleWriteOperationError(e, "create", userDTO.getEmail());
            return null; // This line will never be reached due to exception throwing above
        } catch (Exception e) {
            String errorMsg = "Error creating user: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        logger.debug("Updating user with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (userDTO == null) {
            throw new IllegalArgumentException("UserDTO cannot be null");
        }
        
        // Validate authentication method requirements
        if (!validateAuthenticationMethod(userDTO)) {
            String error = userDTO.getAuthenticationValidationError();
            throw new ValidationException(error != null ? error : "Datos de autenticación inválidos");
        }
        
        // Ensure the ID matches
        userDTO.setUsuarioId(id);

        try {
            UserDTO updatedUser = webClient.put()
                    .uri(USER_BY_ID_ENDPOINT, id)
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedUser != null) {
                logger.info("Successfully updated user with ID: {}", id);
                return updatedUser;
            } else {
                throw new RuntimeException("Backend returned null after user update");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for update", id);
                throw new EntityNotFoundException("Usuario", id.toString());
            }
            
            handleWriteOperationError(e, "update", userDTO.getEmail());
            return null; // This line will never be reached due to exception throwing above
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error updating user " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        try {
            webClient.delete()
                    .uri(USER_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            logger.info("Successfully deleted user with ID: {}", id);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for deletion", id);
                throw new EntityNotFoundException("Usuario", id.toString());
            }
            if (e.getStatusCode().value() == 422) {
                logger.debug("User {} cannot be deleted due to business rules", id);
                throw new BusinessRuleException("No se puede eliminar el usuario porque está activo o tiene dependencias en el sistema");
            }
            
            String errorMsg = String.format("Backend API error while deleting user %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | BusinessRuleException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error deleting user " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO authenticate(String email, String password) {
        logger.debug("Authenticating user: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            Map<String, String> credentials = new HashMap<>();
            credentials.put("email", email.trim());
            credentials.put("password", password);

            UserDTO authenticatedUser = webClient.post()
                    .uri(USER_AUTHENTICATE_ENDPOINT)
                    .bodyValue(credentials)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (authenticatedUser != null) {
                logger.info("User authenticated successfully: {}", email);
                return authenticatedUser;
            } else {
                throw new BusinessRuleException("Credenciales inválidas");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for authentication", email);
                throw new EntityNotFoundException("Usuario", email);
            }
            if (e.getStatusCode().value() == 422) {
                logger.debug("Authentication failed for user {}: invalid credentials or inactive account", email);
                throw new BusinessRuleException("Credenciales inválidas o cuenta inactiva");
            }
            
            String errorMsg = String.format("Backend API error during authentication for %s: %s - %s", 
                    email, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | BusinessRuleException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error authenticating user " + email + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO updateUserStatus(Long id, String newStatus) {
        logger.debug("Updating user {} status to: {}", id, newStatus);
        
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        
        // Validate status value
        Set<String> validStatuses = Set.of("PENDIENTE", "ACTIVO", "INACTIVO", "BLOQUEADO");
        if (!validStatuses.contains(newStatus.trim().toUpperCase())) {
            throw new ValidationException("Estado inválido. Valores válidos: " + validStatuses);
        }

        try {
            Map<String, String> statusUpdate = new HashMap<>();
            statusUpdate.put("estadoCuenta", newStatus.trim().toUpperCase());

            UserDTO updatedUser = webClient.patch()
                    .uri(USER_STATUS_ENDPOINT, id)
                    .bodyValue(statusUpdate)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedUser != null) {
                logger.info("Successfully updated user {} status to {}", id, newStatus);
                return updatedUser;
            } else {
                throw new RuntimeException("Backend returned null after status update");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for status update", id);
                throw new EntityNotFoundException("Usuario", id.toString());
            }
            if (e.getStatusCode().value() == 422) {
                logger.debug("Invalid status transition for user {}: {}", id, newStatus);
                throw new BusinessRuleException("Transición de estado inválida. Verifique el estado actual del usuario.");
            }
            
            String errorMsg = String.format("Backend API error while updating user %s status: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | BusinessRuleException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error updating user " + id + " status: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO assignRolesToUser(Long userId, Set<String> roleNames) {
        logger.debug("Assigning roles to user {}: {}", userId, roleNames);
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (roleNames == null) {
            roleNames = new HashSet<>();
        }

        try {
            Map<String, Object> rolesUpdate = new HashMap<>();
            rolesUpdate.put("roles", new ArrayList<>(roleNames));
            rolesUpdate.put("operation", "replace");

            UserDTO updatedUser = webClient.put()
                    .uri(USER_ROLES_ENDPOINT, userId)
                    .bodyValue(rolesUpdate)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedUser != null) {
                logger.info("Successfully assigned roles to user {}: {}", userId, roleNames);
                return updatedUser;
            } else {
                throw new RuntimeException("Backend returned null after role assignment");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for role assignment", userId);
                throw new EntityNotFoundException("Usuario", userId.toString());
            }
            if (e.getStatusCode().value() == 400) {
                logger.debug("Invalid roles for user {}: {}", userId, roleNames);
                throw new ValidationException("Uno o más roles son inválidos: " + roleNames);
            }
            
            String errorMsg = String.format("Backend API error while assigning roles to user %s: %s - %s", 
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | ValidationException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error assigning roles to user " + userId + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public UserDTO addRolesToUser(Long userId, Set<String> roleNames) {
        logger.debug("Adding roles to user {}: {}", userId, roleNames);
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Role names cannot be null or empty");
        }

        try {
            Map<String, Object> rolesUpdate = new HashMap<>();
            rolesUpdate.put("roles", new ArrayList<>(roleNames));
            rolesUpdate.put("operation", "add");

            UserDTO updatedUser = webClient.patch()
                    .uri(USER_ROLES_ENDPOINT, userId)
                    .bodyValue(rolesUpdate)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedUser != null) {
                logger.info("Successfully added roles to user {}: {}", userId, roleNames);
                return updatedUser;
            } else {
                throw new RuntimeException("Backend returned null after adding roles");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for adding roles", userId);
                throw new EntityNotFoundException("Usuario", userId.toString());
            }
            if (e.getStatusCode().value() == 400) {
                logger.debug("Invalid roles for user {}: {}", userId, roleNames);
                throw new ValidationException("Uno o más roles son inválidos: " + roleNames);
            }
            
            String errorMsg = String.format("Backend API error while adding roles to user %s: %s - %s", 
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | ValidationException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error adding roles to user " + userId + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
    @Override
    public UserDTO removeRolesFromUser(Long userId, Set<String> roleNames) {
        logger.debug("Removing roles from user {}: {}", userId, roleNames);
        
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (roleNames == null || roleNames.isEmpty()) {
            throw new IllegalArgumentException("Role names cannot be null or empty");
        }

        try {
            Map<String, Object> rolesUpdate = new HashMap<>();
            rolesUpdate.put("roles", new ArrayList<>(roleNames));
            rolesUpdate.put("operation", "remove");

            UserDTO updatedUser = webClient.patch()
                    .uri(USER_ROLES_ENDPOINT, userId)
                    .bodyValue(rolesUpdate)
                    .retrieve()
                    .bodyToMono(UserDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedUser != null) {
                logger.info("Successfully removed roles from user {}: {}", userId, roleNames);
                return updatedUser;
            } else {
                throw new RuntimeException("Backend returned null after removing roles");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} not found for removing roles", userId);
                throw new EntityNotFoundException("Usuario", userId.toString());
            }
            
            String errorMsg = String.format("Backend API error while removing roles from user %s: %s - %s", 
                    userId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error removing roles from user " + userId + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<UserDTO> getUsersByRole(String roleName) {
        logger.debug("Retrieving users with role: {}", roleName);
        
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }

        try {
            // Try paginated response first, fall back to direct list if needed
            Object response = webClient.get()
                    .uri(USER_BY_ROLE_ENDPOINT, roleName.trim())
                    .retrieve()
                    .bodyToMono(Object.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (response != null) {
                // Check if response is paginated (has 'content' field)
                if (response instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pageMap = (Map<String, Object>) response;
                    if (pageMap.containsKey("content")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");
                        List<UserDTO> users = content.stream()
                                .map(this::mapToUserDTO)
                                .toList();
                        logger.info("Retrieved {} users with role {} (paginated)", users.size(), roleName);
                        return users;
                    }
                }
                // Direct list response
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> userMaps = (List<Map<String, Object>>) response;
                List<UserDTO> users = userMaps.stream()
                        .map(this::mapToUserDTO)
                        .toList();
                logger.info("Retrieved {} users with role {}", users.size(), roleName);
                return users;
            } else {
                logger.warn("Backend returned null for users with role {}", roleName);
                return List.of();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving users by role %s: %s - %s", 
                    roleName, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving users by role " + roleName + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean userExists(Long id) {
        logger.debug("Checking if user exists: {}", id);
        
        if (id == null) {
            return false;
        }

        try {
            webClient.head()
                    .uri(USER_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.debug("User {} exists in backend", id);
            return true;
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("User {} does not exist", id);
                return false;
            }
            
            // For other errors, assume it exists to be safe
            logger.warn("Error checking user existence for {}: {}", id, e.getMessage());
            return true;
        } catch (Exception e) {
            logger.warn("Error checking user existence for {}: {}", id, e.getMessage());
            return true; // Assume exists on error to be safe
        }
    }
    @Override
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        logger.debug("Checking email availability: {} (excluding user: {})", email, excludeUserId);
        
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        try {
            String uri = USER_EMAIL_CHECK_ENDPOINT + "?email=" + email.trim();
            if (excludeUserId != null) {
                uri += "&excludeUserId=" + excludeUserId;
            }

            Map<String, Boolean> response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Boolean>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            boolean available = response != null && Boolean.TRUE.equals(response.get("available"));
            logger.debug("Email {} availability: {}", email, available);
            return available;
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while checking email availability: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error checking email availability: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean validateAuthenticationMethod(UserDTO userDTO) {
        if (userDTO == null) {
            return false;
        }
        return userDTO.isValidAuthentication();
    }

    @Override
    public Map<String, Long> getUserCountByStatus() {
        logger.debug("Retrieving user count by status");
        
        try {
            Map<String, Long> counts = webClient.get()
                    .uri(USER_COUNT_BY_STATUS_ENDPOINT)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Long>>() {})
                    .retryWhen(retryConfiguration)
                    .block();

            if (counts != null) {
                logger.info("Retrieved user counts by status: {}", counts);
                return counts;
            } else {
                logger.warn("Backend returned null for user counts by status");
                return new HashMap<>();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving user counts: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving user counts: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<UserDTO> getRecentlyCreatedUsers(int days) {
        logger.debug("Retrieving users created in last {} days", days);
        
        if (days < 0) {
            throw new IllegalArgumentException("Days must be non-negative");
        }

        try {
            // Try paginated response first, fall back to direct list if needed
            Object response = webClient.get()
                    .uri(USER_RECENT_ENDPOINT, days)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (response != null) {
                // Check if response is paginated (has 'content' field)
                if (response instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pageMap = (Map<String, Object>) response;
                    if (pageMap.containsKey("content")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");
                        List<UserDTO> users = content.stream()
                                .map(this::mapToUserDTO)
                                .toList();
                        logger.info("Retrieved {} users created in last {} days (paginated)", users.size(), days);
                        return users;
                    }
                }
                // Direct list response
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> userMaps = (List<Map<String, Object>>) response;
                List<UserDTO> users = userMaps.stream()
                        .map(this::mapToUserDTO)
                        .toList();
                logger.info("Retrieved {} users created in last {} days", users.size(), days);
                return users;
            } else {
                logger.warn("Backend returned null for recent users");
                return List.of();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving recent users: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving recent users: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public List<UserDTO> getPendingUsers() {
        logger.debug("Retrieving pending users");
        
        try {
            // Try paginated response first, fall back to direct list if needed
            Object response = webClient.get()
                    .uri(USER_PENDING_ENDPOINT)
                    .retrieve()
                    .bodyToMono(Object.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (response != null) {
                // Check if response is paginated (has 'content' field)
                if (response instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> pageMap = (Map<String, Object>) response;
                    if (pageMap.containsKey("content")) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> content = (List<Map<String, Object>>) pageMap.get("content");
                        List<UserDTO> users = content.stream()
                                .map(this::mapToUserDTO)
                                .toList();
                        logger.info("Retrieved {} pending users (paginated)", users.size());
                        return users;
                    }
                }
                // Direct list response
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> userMaps = (List<Map<String, Object>>) response;
                List<UserDTO> users = userMaps.stream()
                        .map(this::mapToUserDTO)
                        .toList();
                logger.info("Retrieved {} pending users", users.size());
                return users;
            } else {
                logger.warn("Backend returned null for pending users");
                return List.of();
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving pending users: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving pending users: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Handles errors from write operations (create, update) with proper exception mapping.
     * 
     * @param e the WebClientResponseException
     * @param operation the operation being performed ("create" or "update")
     * @param userEmail the user email for context
     * @throws ConflictException for 409 status (duplicate email)
     * @throws ValidationException for 400 status (validation errors)
     * @throws BusinessRuleException for 422 status (business rule violations)
     * @throws RuntimeException for other errors
     */
    private void handleWriteOperationError(WebClientResponseException e, String operation, String userEmail) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        
        switch (statusCode) {
            case 409:
                logger.debug("Conflict error during user {}: duplicate email '{}'", operation, userEmail);
                throw new ConflictException("Ya existe un usuario con el email '" + userEmail + "'");
                
            case 400:
                logger.debug("Validation error during user {}: {}", operation, responseBody);
                throw new ValidationException("Datos inválidos para el usuario: " + responseBody);
                
            case 422:
                logger.debug("Business rule violation during user {}: {}", operation, responseBody);
                throw new BusinessRuleException("Regla de negocio violada: " + responseBody);
                
            default:
                String errorMsg = String.format("Backend API error during user %s: %s - %s", 
                        operation, e.getStatusCode(), responseBody);
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Maps a Map to UserDTO object.
     * Simple mapping utility for handling raw Map responses from backend.
     * 
     * @param map the map containing user data
     * @return mapped UserDTO object
     */
    private UserDTO mapToUserDTO(Map<String, Object> map) {
        UserDTO user = new UserDTO();
        
        try {
            if (map.get("usuarioId") != null) {
                user.setUsuarioId(((Number) map.get("usuarioId")).longValue());
            }
            user.setNombres((String) map.get("nombres"));
            user.setApellidos((String) map.get("apellidos"));
            user.setEmail((String) map.get("email"));
            user.setEstadoCuenta((String) map.get("estadoCuenta"));
            user.setMetodoAutenticacion((String) map.get("metodoAutenticacion"));
            user.setSsoProvider((String) map.get("ssoProvider"));
            
            // Map ssoProviderId/ssoSubjectId (backend field name)
            if (map.get("ssoSubjectId") != null) {
                user.setSsoSubjectId((String) map.get("ssoSubjectId"));
            }
            
            if (map.get("negocioId") != null) {
                user.setNegocioId(((Number) map.get("negocioId")).longValue());
            }
            
            if (map.get("roles") != null) {
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) map.get("roles");
                user.setRoles(new java.util.HashSet<>(roles));
            }
            
            // Parse date fields if present
            if (map.get("fechaCreacion") != null) {
                String fechaStr = (String) map.get("fechaCreacion");
                user.setFechaCreacion(java.time.LocalDateTime.parse(fechaStr));
            }
            
            if (map.get("ultimoLogin") != null) {
                String fechaStr = (String) map.get("ultimoLogin");
                user.setUltimoLogin(java.time.LocalDateTime.parse(fechaStr));
            }
            
            if (map.get("habilitado") != null) {
                user.setHabilitado((Boolean) map.get("habilitado"));
            }
            
            if (map.get("usuarioCreacion") != null) {
                user.setUsuarioCreacion((String) map.get("usuarioCreacion"));
            }
            
        } catch (Exception e) {
            logger.warn("Error mapping user data: {}", e.getMessage());
        }
        
        return user;
    }

    /**
     * Response class for paginated user results.
     */
    public static class UserPageResponse {
        private List<UserDTO> content;
        private long totalElements;
        private int totalPages;
        private int size;
        private int number;
        
        // Default constructor
        public UserPageResponse() {
            this.content = new ArrayList<>();
        }

        public List<UserDTO> getContent() {
            return content;
        }

        public void setContent(List<UserDTO> content) {
            this.content = content;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}