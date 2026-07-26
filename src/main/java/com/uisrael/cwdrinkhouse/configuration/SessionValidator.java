package com.uisrael.cwdrinkhouse.configuration;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * DEPRECATED: Legacy session validator component.
 * 
 * This class was used for custom session validation before Spring Security integration.
 * It is no longer used as authentication and authorization are now handled by:
 * - Spring Security's SecurityContext for authentication state
 * - @PreAuthorize annotations for method-level authorization
 * - BaseController helper methods for role checking
 * 
 * This class is kept for potential future reference but should not be used in new code.
 * Use Spring Security patterns instead.
 * 
 * @deprecated Use Spring Security SecurityContext and @PreAuthorize annotations instead
 */
@Component
@Deprecated
public class SessionValidator {

    private static final String USER_SESSION_ATTRIBUTE = "userSession";
    private static final String LAST_ACTIVITY_TIME = "lastActivityTime";
    private static final long SESSION_TIMEOUT_MILLIS = 30 * 60 * 1000; // 30 minutes

    /**
     * Validates if a session is currently active and valid.
     * 
     * @param session the HTTP session to validate
     * @return true if session is valid, false otherwise
     */
    public boolean isSessionValid(HttpSession session) {
        if (session == null) {
            return false;
        }

        // Check if user session attribute exists
        Object userSession = session.getAttribute(USER_SESSION_ATTRIBUTE);
        if (userSession == null) {
            return false;
        }

        // Check if session has timed out
        return !hasSessionTimedOut(session);
    }

    /**
     * Checks if a session has exceeded the timeout threshold.
     * 
     * @param session the HTTP session to check
     * @return true if session has timed out, false otherwise
     */
    public boolean hasSessionTimedOut(HttpSession session) {
        if (session == null) {
            return true;
        }

        Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
        if (lastActivityTime == null) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastActivity = currentTime - lastActivityTime;
        return timeSinceLastActivity > SESSION_TIMEOUT_MILLIS;
    }

    /**
     * Updates the last activity timestamp for the session.
     * 
     * @param session the HTTP session to update
     */
    public void updateLastActivityTime(HttpSession session) {
        if (session != null) {
            session.setAttribute(LAST_ACTIVITY_TIME, System.currentTimeMillis());
        }
    }

    /**
     * Initializes a new session with required attributes.
     * 
     * @param session the HTTP session to initialize
     * @param userSession the user session data
     */
    public void initializeSession(HttpSession session, Object userSession) {
        session.setAttribute(USER_SESSION_ATTRIBUTE, userSession);
        session.setAttribute(LAST_ACTIVITY_TIME, System.currentTimeMillis());
        session.setMaxInactiveInterval(30 * 60); // 30 minutes
    }

    /**
     * Retrieves the user session from HTTP session.
     * 
     * @param session the HTTP session
     * @return the user session object, or null if not found
     */
    public Object getUserSession(HttpSession session) {
        if (session == null) {
            return null;
        }
        return session.getAttribute(USER_SESSION_ATTRIBUTE);
    }

    /**
     * Invalidates and clears a session.
     * 
     * @param session the HTTP session to invalidate
     */
    public void invalidateSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute(USER_SESSION_ATTRIBUTE);
            session.removeAttribute(LAST_ACTIVITY_TIME);
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }

    /**
     * Checks if current user is authenticated.
     * 
     * @return true if user is authenticated, false otherwise
     */
    public boolean isUserAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated() &&
               !SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser");
    }

    /**
     * Gets the timeout duration in seconds.
     * 
     * @return session timeout in seconds
     */
    public int getSessionTimeoutSeconds() {
        return (int) (SESSION_TIMEOUT_MILLIS / 1000);
    }

    /**
     * Gets remaining time for current session in milliseconds.
     * 
     * @param session the HTTP session
     * @return remaining time in milliseconds, or -1 if session is invalid
     */
    public long getRemainingSessionTime(HttpSession session) {
        if (!isSessionValid(session)) {
            return -1;
        }

        Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);
        if (lastActivityTime == null) {
            return SESSION_TIMEOUT_MILLIS;
        }

        long currentTime = System.currentTimeMillis();
        long timeSinceLastActivity = currentTime - lastActivityTime;
        long remaining = SESSION_TIMEOUT_MILLIS - timeSinceLastActivity;
        return remaining > 0 ? remaining : 0;
    }
}
