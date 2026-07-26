package com.uisrael.cwdrinkhouse.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Custom authentication success handler that populates session attributes
 * required by Thymeleaf templates after successful login.
 */
@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        // Populate session attributes from authentication
        populateSessionAttributes(request.getSession(), authentication);
        
        // Continue with default success handling (redirect to saved request or default success URL)
        super.onAuthenticationSuccess(request, response, authentication);
    }

    /**
     * Populates session attributes required by Thymeleaf templates from Spring Security authentication.
     * 
     * @param session the HTTP session to populate
     * @param authentication the Spring Security authentication object
     */
    private void populateSessionAttributes(HttpSession session, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        
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
        session.setAttribute("loginTime", LocalDateTime.now());
        
        // Set userSession attribute for compatibility with existing code
        session.setAttribute("userSession", username);
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
}