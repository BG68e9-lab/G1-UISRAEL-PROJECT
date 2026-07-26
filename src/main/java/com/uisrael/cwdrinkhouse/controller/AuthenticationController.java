package com.uisrael.cwdrinkhouse.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling user authentication (login/logout).
 * Simplified to work with Spring Security's built-in authentication.
 */
// @Controller  // DISABLED - Using SimpleAuthController instead
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /**
     * Show login page with error handling.
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                               @RequestParam(value = "logout", required = false) String logout,
                               Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Credenciales inválidas. Verifique su email y contraseña.");
        }
        
        if (logout != null) {
            model.addAttribute("logoutMessage", "Ha cerrado sesión exitosamente.");
        }

        return "login";
    }

    /**
     * Root path handler - redirect to dashboard if authenticated, otherwise to login.
     */
    @GetMapping("/")
    public String root(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard";
        }
        return "redirect:/login";
    }
}