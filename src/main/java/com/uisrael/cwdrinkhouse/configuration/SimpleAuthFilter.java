package com.uisrael.cwdrinkhouse.configuration;

import com.uisrael.cwdrinkhouse.dto.UserSessionDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Simple authentication filter that checks session for logged in user.
 * Replaces Spring Security with basic session-based authentication.
 */
@Component
public class SimpleAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthFilter.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        
        // Remove context path from URI for comparison
        if (contextPath != null && !contextPath.isEmpty()) {
            requestURI = requestURI.substring(contextPath.length());
        }
        
        logger.debug("Processing request: {}", requestURI);

        // Allow access to public resources
        if (isPublicResource(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null || !isUserAuthenticated(session)) {
            logger.debug("User not authenticated, redirecting to login");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Update last activity time
        updateLastActivityTime(session);

        // User is authenticated, continue with request
        filterChain.doFilter(request, response);
    }

    /**
     * Check if the requested resource is public (doesn't require authentication).
     */
    private boolean isPublicResource(String uri) {
        return uri.equals("/") ||
               uri.equals("/login") ||
               uri.equals("/dashboard") ||
               uri.startsWith("/static/") ||
               uri.startsWith("/css/") ||
               uri.startsWith("/js/") ||
               uri.startsWith("/images/") ||
               uri.startsWith("/webjars/") ||
               uri.startsWith("/error") ||
               uri.startsWith("/favicon.ico");
    }

    /**
     * Check if user is authenticated by looking for valid session user.
     */
    private boolean isUserAuthenticated(HttpSession session) {
        try {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            return currentUser != null && currentUser.isActive();
        } catch (Exception e) {
            logger.warn("Error checking authentication", e);
            return false;
        }
    }

    /**
     * Update the last activity time in session.
     */
    private void updateLastActivityTime(HttpSession session) {
        try {
            session.setAttribute("lastActivityTime", LocalDateTime.now());
        } catch (Exception e) {
            logger.warn("Error updating last activity time", e);
        }
    }
}