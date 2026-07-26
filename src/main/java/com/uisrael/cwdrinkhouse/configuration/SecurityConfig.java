package com.uisrael.cwdrinkhouse.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Disabled Spring Security configuration.
 * Using simple session-based authentication instead.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Disable Spring Security for all requests.
     * Authentication is handled by SimpleAuthFilter.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()  // Allow all requests
            )
            .csrf(csrf -> csrf.disable())    // Disable CSRF
            .formLogin(form -> form.disable())  // Disable form login
            .httpBasic(basic -> basic.disable())  // Disable basic auth
            .logout(logout -> logout.disable());   // Disable logout

        return http.build();
    }

    /**
     * Password encoder bean for checking passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
