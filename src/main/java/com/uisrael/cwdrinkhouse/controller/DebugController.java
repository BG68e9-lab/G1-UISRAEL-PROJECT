package com.uisrael.cwdrinkhouse.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DebugController {
    
    @GetMapping("/debug/auth")
    @ResponseBody
    public String debugAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null) {
            return "NO AUTHENTICATION FOUND";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("User: ").append(auth.getName()).append("\n");
        sb.append("Authenticated: ").append(auth.isAuthenticated()).append("\n");
        sb.append("Principal: ").append(auth.getPrincipal()).append("\n");
        sb.append("Authorities: ").append(auth.getAuthorities()).append("\n");
        
        return sb.toString();
    }
}
