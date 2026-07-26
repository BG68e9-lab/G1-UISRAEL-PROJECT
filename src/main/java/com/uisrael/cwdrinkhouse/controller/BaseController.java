package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.UserSessionDTO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base controller for common functionality.
 * Updated to work with simple session-based authentication.
 */
public abstract class BaseController {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * Utility method to check if the current user is authenticated via session.
     */
    protected boolean isAuthenticated(HttpSession session) {
        try {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            return currentUser != null && currentUser.isActive();
        } catch (Exception e) {
            logger.warn("Error checking authentication", e);
            return false;
        }
    }
    
    /**
     * Utility method to check if the current user has ADMIN role.
     */
    protected boolean hasAdminRole(HttpSession session) {
        try {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            return currentUser != null && currentUser.hasRole("ADMIN");
        } catch (Exception e) {
            logger.warn("Error checking admin role", e);
            return false;
        }
    }
    
    /**
     * Utility method to check if the current user has EMPLEADO role or higher.
     */
    protected boolean hasEmpleadoRole(HttpSession session) {
        try {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            return currentUser != null && (currentUser.hasRole("ADMIN") || currentUser.hasRole("EMPLEADO"));
        } catch (Exception e) {
            logger.warn("Error checking empleado role", e);
            return false;
        }
    }
    
    /**
     * Get the current user from session.
     */
    protected UserSessionDTO getCurrentUser(HttpSession session) {
        try {
            return (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
        } catch (Exception e) {
            logger.warn("Error getting current user", e);
            return null;
        }
    }
    
    /**
     * Get the current user ID from session.
     */
    protected Long getCurrentUserId(HttpSession session) {
        UserSessionDTO currentUser = getCurrentUser(session);
        return currentUser != null ? currentUser.getUserId() : null;
    }
}