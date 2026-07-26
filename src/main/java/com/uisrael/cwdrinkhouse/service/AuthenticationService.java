package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.UserSessionDTO;
import java.util.Optional;

/**
 * Service interface for user authentication.
 * 
 * Provides authentication capabilities including:
 * - User credential validation
 * - Session creation and management
 * - Integration with backend authentication API
 * 
 * Requirements addressed: 1.2, 1.3, 18.1, 18.8
 */
public interface AuthenticationService {

    /**
     * Authenticate user with email and password.
     * 
     * @param email    user's email address
     * @param password user's password
     * @return Optional containing UserSessionDTO if authentication successful, empty otherwise
     */
    Optional<UserSessionDTO> authenticate(String email, String password);

    /**
     * Validate existing session token with backend.
     * 
     * @param sessionToken the session token to validate
     * @return true if token is valid, false otherwise
     */
    boolean validateSession(String sessionToken);

    /**
     * Logout user and invalidate session.
     * 
     * @param sessionToken the session token to invalidate
     */
    void logout(String sessionToken);
}