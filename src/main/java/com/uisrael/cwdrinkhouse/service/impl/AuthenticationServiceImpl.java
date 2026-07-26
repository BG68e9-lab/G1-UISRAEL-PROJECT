package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.UserSessionDTO;
import com.uisrael.cwdrinkhouse.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * Simple database-based authentication service.
 * Queries users directly from the database without external API.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<UserSessionDTO> authenticate(String email, String password) {
        try {
            logger.debug("Attempting database authentication for user: {}", email);

            // Query user from database
            String sql = """
                SELECT u.usuario_id, u.email, u.nombre_completo, u.password_hash, 
                       u.estado_cuenta, u.negocio_id, n.nombre as nombre_negocio, r.nombre as rol_nombre
                FROM usuarios u 
                LEFT JOIN negocios n ON u.negocio_id = n.negocio_id
                LEFT JOIN roles r ON u.rol_id = r.rol_id
                WHERE u.email = ? AND u.estado_cuenta = 'ACTIVO'
            """;

            var userRows = jdbcTemplate.queryForList(sql, email);
            
            if (userRows.isEmpty()) {
                logger.warn("User not found or not active: {}", email);
                return Optional.empty();
            }

            var userRow = userRows.get(0);
            String storedHash = (String) userRow.get("password_hash");

            // Verify password (temporarily without BCrypt for testing)
            if (storedHash == null || !storedHash.equals(password)) {
                logger.warn("Invalid password for user: {}", email);
                return Optional.empty();
            }

            // Create user session
            UserSessionDTO session = new UserSessionDTO();
            session.setUserId(((Number) userRow.get("usuario_id")).longValue());
            session.setEmail((String) userRow.get("email"));
            session.setNombreCompleto((String) userRow.get("nombre_completo"));
            
            Object negocioIdObj = userRow.get("negocio_id");
            if (negocioIdObj != null) {
                session.setNegocioId(((Number) negocioIdObj).longValue());
            }
            
            session.setNombreNegocio((String) userRow.get("nombre_negocio"));
            session.setLoginTime(LocalDateTime.now());
            session.setLastActivityTime(LocalDateTime.now());
            session.setActive(true);

            // Add role
            String roleName = (String) userRow.get("rol_nombre");
            if (roleName != null) {
                session.addRole(roleName);
            }

            logger.info("Database authentication successful for user: {}", email);
            return Optional.of(session);

        } catch (Exception e) {
            logger.error("Database authentication error for user: {}", email, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean validateSession(String sessionToken) {
        // For simple session-based auth, we don't use server-side tokens
        // Session validation is handled by SimpleAuthFilter
        return true;
    }

    @Override
    public void logout(String sessionToken) {
        // For simple session-based auth, logout is handled by clearing the session
        // No server-side cleanup needed
        logger.debug("Logout completed (session-based auth)");
    }
}
