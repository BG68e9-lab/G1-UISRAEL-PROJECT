package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.UserDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Set;

/**
 * Service interface for managing users.
 * Provides complete user management with authentication, role management, and state transitions.
 * Handles communication with backend API at localhost:8080.
 * 
 * Features:
 * - Complete CRUD operations for users
 * - Authentication validation for both local and SSO users
 * - User status transitions (PENDIENTE → ACTIVO → INACTIVO)
 * - Role assignment and management
 * - Email duplicate validation
 * - Support for both local password and SSO authentication
 * 
 * Requirements: 1.2-1.3, 8.1-8.14, 16.1, 18.5-18.6
 */
public interface UserService {

    /**
     * Retrieve all users from the backend API with pagination.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return Page of UserDTO objects
     * @throws RuntimeException if backend API call fails
     */
    Page<UserDTO> getAllUsers(int page, int size);

    /**
     * Search users by various criteria with pagination.
     * 
     * @param nombres partial match for first names (optional)
     * @param apellidos partial match for last names (optional) 
     * @param email partial match for email (optional)
     * @param estadoCuenta filter by account status (optional)
     * @param page the page number (0-based)
     * @param size the page size
     * @return Page of UserDTO objects matching the criteria
     * @throws RuntimeException if backend API call fails
     */
    Page<UserDTO> searchUsers(String nombres, String apellidos, String email, 
                             String estadoCuenta, int page, int size);

    /**
     * Retrieve a specific user by ID.
     * 
     * @param id the user ID
     * @return UserDTO for the specified ID
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO getUserById(Long id);

    /**
     * Create a new user in the backend.
     * Validates email uniqueness, authentication method requirements, and creates user with PENDIENTE status.
     * 
     * @param userDTO the user data to create (usuarioId should be null)
     * @return UserDTO with the created user including generated ID and timestamps
     * @throws ConflictException if email already exists (409)
     * @throws ValidationException if userDTO validation fails (400)
     * @throws BusinessRuleException if authentication method validation fails (422)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Update an existing user in the backend.
     * Validates email uniqueness (excluding current user), authentication method, and updates user data.
     * 
     * @param id the ID of the user to update
     * @param userDTO the updated user data
     * @return UserDTO with the updated user data
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws ConflictException if updated email conflicts with existing user (409)
     * @throws ValidationException if userDTO validation fails (400)
     * @throws BusinessRuleException if authentication method validation fails (422)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * Delete a user from the backend.
     * Only allows deletion of users in PENDIENTE or INACTIVO status.
     * 
     * @param id the ID of the user to delete
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws BusinessRuleException if user cannot be deleted due to business rules (422)
     *         e.g., user is ACTIVO or has audit trail dependencies
     * @throws RuntimeException if backend API call fails
     */
    void deleteUser(Long id);

    /**
     * Authenticate user with email and password for local authentication.
     * Validates credentials and returns user information if successful.
     * 
     * @param email the user email
     * @param password the plaintext password
     * @return UserDTO of authenticated user if credentials are valid
     * @throws EntityNotFoundException if user with email does not exist (404)
     * @throws BusinessRuleException if credentials are invalid or user account is not active (422)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO authenticate(String email, String password);

    /**
     * Update user account status with proper state transition validation.
     * Valid transitions: PENDIENTE → ACTIVO, ACTIVO → INACTIVO, INACTIVO → ACTIVO
     * 
     * @param id the user ID
     * @param newStatus the new status (PENDIENTE, ACTIVO, INACTIVO, BLOQUEADO)
     * @return UserDTO with updated status
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws BusinessRuleException if state transition is invalid (422)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO updateUserStatus(Long id, String newStatus);

    /**
     * Assign roles to a user.
     * Replaces all existing roles with the provided set.
     * 
     * @param userId the user ID
     * @param roleNames set of role names to assign
     * @return UserDTO with updated roles
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws ValidationException if any role name is invalid (400)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO assignRolesToUser(Long userId, Set<String> roleNames);

    /**
     * Add roles to a user.
     * Adds roles to existing user roles without removing current ones.
     * 
     * @param userId the user ID
     * @param roleNames set of role names to add
     * @return UserDTO with updated roles
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws ValidationException if any role name is invalid (400)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO addRolesToUser(Long userId, Set<String> roleNames);

    /**
     * Remove roles from a user.
     * Removes specified roles from user's current roles.
     * 
     * @param userId the user ID
     * @param roleNames set of role names to remove
     * @return UserDTO with updated roles
     * @throws EntityNotFoundException if user with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    UserDTO removeRolesFromUser(Long userId, Set<String> roleNames);

    /**
     * Get users by role name.
     * Returns all users that have the specified role.
     * 
     * @param roleName the role name to filter by
     * @return List of UserDTO objects with the specified role
     * @throws RuntimeException if backend API call fails
     */
    List<UserDTO> getUsersByRole(String roleName);

    /**
     * Check if a user exists by ID.
     * 
     * @param id the user ID to check
     * @return true if the user exists, false otherwise
     */
    boolean userExists(Long id);

    /**
     * Check if an email is available (not taken by another user).
     * 
     * @param email the email to check
     * @param excludeUserId user ID to exclude from check (for updates), null for new users
     * @return true if email is available, false if taken
     */
    boolean isEmailAvailable(String email, Long excludeUserId);

    /**
     * Validate user authentication method requirements.
     * Checks that local auth has password and SSO has provider/subject.
     * 
     * @param userDTO the user data to validate
     * @return true if authentication data is valid
     */
    boolean validateAuthenticationMethod(UserDTO userDTO);

    /**
     * Get users count by status.
     * Returns count of users grouped by account status.
     * 
     * @return Map with status as key and count as value
     */
    java.util.Map<String, Long> getUserCountByStatus();

    /**
     * Get recently created users.
     * Returns users created within the specified number of days.
     * 
     * @param days number of days to look back
     * @return List of recently created UserDTO objects
     */
    List<UserDTO> getRecentlyCreatedUsers(int days);

    /**
     * Get users with pending activation.
     * Returns users in PENDIENTE status that need activation.
     * 
     * @return List of UserDTO objects pending activation
     */
    List<UserDTO> getPendingUsers();
}