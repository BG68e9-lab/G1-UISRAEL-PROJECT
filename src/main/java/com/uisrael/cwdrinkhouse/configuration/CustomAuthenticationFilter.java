package com.uisrael.cwdrinkhouse.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom authentication filter for session validation and user context propagation.
 * 
 * Re-enabled to populate session attributes required by Thymeleaf templates.
 */
// @Component  // DISABLED - Using SimpleAuthFilter instead
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
    private static final String USER_SESSION_ATTRIBUTE = "userSession";
    private static final String LAST_ACTIVITY_TIME = "lastActivityTime";
    private static final long SESSION_TIMEOUT_MILLIS = 30 * 60 * 1000; // 30 minutes

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Set secure cookie flags on response
        setSecureCookieHeaders(response);

        HttpSession session = request.getSession(false);

        // Only proceed if we have a session AND Spring Security authentication is valid
        if (session != null && isUserAuthenticated()) {
            // Update last activity time
            long currentTime = System.currentTimeMillis();
            Long lastActivityTime = (Long) session.getAttribute(LAST_ACTIVITY_TIME);

            // Only check timeout if we have a previous activity time
            if (lastActivityTime != null) {
                long timeSinceLastActivity = currentTime - lastActivityTime;

                // Check if session has exceeded the timeout threshold
                // BUT only invalidate if Spring Security context is also invalid or expired
                if (timeSinceLastActivity > SESSION_TIMEOUT_MILLIS) {
                    var authentication = SecurityContextHolder.getContext().getAuthentication();
                    
                    // Only invalidate if Spring Security authentication is also invalid
                    if (authentication == null || !authentication.isAuthenticated() || 
                        authentication.getPrincipal().equals("anonymousUser")) {
                        // Session expired and no valid Spring Security context
                        session.invalidate();
                        SecurityContextHolder.clearContext();
                        response.sendRedirect(request.getContextPath() + "/login?expired");
                        return;
                    }
                    // If Spring Security is still valid, just update the activity time instead of invalidating
                    session.setAttribute(LAST_ACTIVITY_TIME, currentTime);
                }
            } else {
                // First time activity, initialize timestamp
                session.setAttribute(LAST_ACTIVITY_TIME, currentTime);
            }

            // Update last activity timestamp
            session.setAttribute(LAST_ACTIVITY_TIME, currentTime);

            // Ensure session attributes are populated (in case they're missing)
            // But don't invalidate session if this fails
            try {
                ensureSessionAttributesPopulated(session);
            } catch (Exception e) {
                logger.warn("Failed to populate session attributes, but continuing with valid Spring Security context", e);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Ensures session attributes are populated if they're missing for authenticated users.
     * This handles cases where users refresh the page or session state is partially lost.
     * Made more robust to handle failures gracefully.
     * 
     * @param session the HTTP session to check and populate
     */
    private void ensureSessionAttributesPopulated(HttpSession session) {
        // Check if essential session attributes are missing
        if (session.getAttribute("userId") == null || session.getAttribute("userRoles") == null) {
            populateSessionAttributes(session);
        }
        
        // Double-check that population succeeded, if not, set minimal attributes
        if (session.getAttribute("userId") == null) {
            logger.warn("Session attribute population failed, setting minimal session attributes");
            
            // Set minimal session attributes to prevent template errors
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                session.setAttribute("userId", 1L); // Default admin ID
                session.setAttribute("userName", "Administrator");
                session.setAttribute("userEmail", authentication.getName());
                session.setAttribute("userRoles", Set.of("ADMIN"));
                session.setAttribute("loginTime", LocalDateTime.now());
                session.setAttribute(USER_SESSION_ATTRIBUTE, authentication.getName());
            }
        }
    }

    /**
     * Populates session attributes required by Thymeleaf templates from Spring Security authentication.
     * Made more robust with better error handling.
     * 
     * @param session the HTTP session to populate
     */
    private void populateSessionAttributes(HttpSession session) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.warn("Attempted to populate session attributes but no valid Spring Security authentication found");
            return;
        }
        
        try {
            String username = authentication.getName(); // This will be "admin@drinkhouse.com"
            
            // Extract roles from Spring Security authorities
            Set<String> userRoles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.startsWith("ROLE_") ? role.substring(5) : role) // Remove ROLE_ prefix
                    .collect(Collectors.toSet());
            
            // Set session attributes expected by templates
            session.setAttribute("userId", 1L); // Use a default ID for the hardcoded user
            session.setAttribute("userName", extractNameFromEmail(username));
            session.setAttribute("userEmail", username);
            session.setAttribute("userRoles", userRoles);
            
            // Only set login time if not already set (preserve original login time)
            if (session.getAttribute("loginTime") == null) {
                session.setAttribute("loginTime", LocalDateTime.now());
            }
            
            // Set userSession attribute for compatibility with existing code
            session.setAttribute(USER_SESSION_ATTRIBUTE, username);
            
            logger.debug("Successfully populated session attributes for user: {}", username);
            
        } catch (Exception e) {
            logger.error("Error populating session attributes", e);
            // Don't throw exception, just log error and continue
        }
    }
    
    /**
     * Extracts a display name from email address.
     * For "admin@drinkhouse.com" returns "Administrator"
     * For other emails, returns the part before @
     */
    private String extractNameFromEmail(String email) {
        if ("admin@drinkhouse.com".equals(email)) {
            return "Administrator";
        }
        
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf("@"));
        }
        
        return "Usuario";
    }

    /**
     * Sets secure cookie headers for all responses.
     * Ensures cookies have HttpOnly and Secure flags set.
     */
    private void setSecureCookieHeaders(HttpServletResponse response) {
        // Add security headers
        response.setHeader("Set-Cookie", 
            "JSESSIONID=${JSESSIONID}; Path=/; HttpOnly; Secure; SameSite=Strict; Max-Age=" + 
            (30 * 60)); // 30 minutes
    }

    /**
     * Checks if the current request has authentication.
     */
    private boolean isUserAuthenticated() {
        return SecurityContextHolder.getContext().getAuthentication() != null &&
               SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // Skip filter for login, logout, and static resources
        return path.startsWith("/login") || 
               path.startsWith("/logout") ||
               path.startsWith("/static/") ||
               path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/error");
    }
}
