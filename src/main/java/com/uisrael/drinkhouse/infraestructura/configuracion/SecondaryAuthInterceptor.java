package com.uisrael.drinkhouse.infraestructura.configuracion;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecondaryAuthInterceptor implements HandlerInterceptor {

    private final ICodigoAccesoUseCase codigoAccesoUseCase;

    public SecondaryAuthInterceptor(ICodigoAccesoUseCase codigoAccesoUseCase) {
        this.codigoAccesoUseCase = codigoAccesoUseCase;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
            throws Exception {
        
        String requestPath = request.getRequestURI();
        
        if (requiresSecondaryAuth(requestPath, request.getMethod())) {
            String secondaryAuthToken = request.getHeader("X-Secondary-Auth");
            
            if (secondaryAuthToken == null || secondaryAuthToken.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"timestamp\":\"" + java.time.OffsetDateTime.now() + "\"," +
                    "\"status\":401," +
                    "\"error\":\"Unauthorized\"," +
                    "\"message\":\"Autenticación secundaria inválida: header X-Secondary-Auth requerido\"," +
                    "\"path\":\"" + requestPath + "\"}"
                );
                return false;
            }
            
            try {
                CodigoAcceso codigo = codigoAccesoUseCase.validarCodigo(secondaryAuthToken);
                request.setAttribute("codigoAccesoValidado", codigo);
                return true;
            } catch (RecursoNoEncontradoException | IllegalStateException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"timestamp\":\"" + java.time.OffsetDateTime.now() + "\"," +
                    "\"status\":401," +
                    "\"error\":\"Unauthorized\"," +
                    "\"message\":\"Autenticación secundaria inválida\"," +
                    "\"path\":\"" + requestPath + "\"}"
                );
                return false;
            }
        }
        
        return true;
    }

private boolean requiresSecondaryAuth(String path, String method) {
        return "POST".equalsIgnoreCase(method) && 
               path.matches(".*/api/v1/movimientos/con-auditoria");
    }
}
