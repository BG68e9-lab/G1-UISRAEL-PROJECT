package com.uisrael.cwdrinkhouse.controller;

import com.uisrael.cwdrinkhouse.dto.UserSessionDTO;
import com.uisrael.cwdrinkhouse.service.AuthenticationService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Simple authentication controller without Spring Security.
 * Handles login, logout, and session management using plain session.
 */
@Controller
public class SimpleAuthController {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAuthController.class);
    private static final String SESSION_USER_KEY = "currentUser";

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * Show login page.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                          @RequestParam(value = "logout", required = false) String logout,
                          @RequestParam(value = "expired", required = false) String expired,
                          Model model, HttpSession session) {
        
        // If already logged in, redirect to dashboard
        if (isLoggedIn(session)) {
            return "redirect:/dashboard";
        }
        
        if (error != null) {
            model.addAttribute("errorMessage", "Email o contraseña incorrectos");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "Has cerrado sesión exitosamente");
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente");
        }
        
        return "login";
    }

    /**
     * Process login form.
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        
        try {
            logger.info("Attempting login for email: {}", email);
            
            var authResult = authenticationService.authenticate(email, password);
            
            if (authResult.isPresent()) {
                UserSessionDTO userSession = authResult.get();
                
                // Create session
                session.setAttribute(SESSION_USER_KEY, userSession);
                session.setAttribute("userId", userSession.getUserId());
                session.setAttribute("userName", userSession.getNombreCompleto());
                session.setAttribute("userEmail", userSession.getEmail());
                session.setAttribute("userRoles", userSession.getRoles());
                session.setAttribute("loginTime", userSession.getLoginTime());
                session.setAttribute("lastActivityTime", LocalDateTime.now());
                
                // Set session timeout to 30 minutes
                session.setMaxInactiveInterval(30 * 60);
                
                logger.info("Login successful for user: {}", email);
                return "redirect:/dashboard";
                
            } else {
                logger.warn("Login failed for email: {}", email);
                redirectAttributes.addAttribute("error", "true");
                return "redirect:/login";
            }
            
        } catch (Exception e) {
            logger.error("Login error for email: " + email, e);
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    /**
     * Process logout.
     */
    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            logger.info("User logging out");
            
            // Clear session
            session.invalidate();
            
            redirectAttributes.addAttribute("logout", "true");
            return "redirect:/login";
            
        } catch (Exception e) {
            logger.error("Logout error", e);
            redirectAttributes.addAttribute("error", "true");
            return "redirect:/login";
        }
    }

    /**
     * Root redirect: siempre a dashboard público
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    /**
     * Check if user is logged in.
     */
    private boolean isLoggedIn(HttpSession session) {
        try {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute(SESSION_USER_KEY);
            return currentUser != null && currentUser.isActive();
        } catch (Exception e) {
            logger.warn("Error checking login status", e);
            return false;
        }
    }
}