package com.uisrael.cwdrinkhouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

/**
 * Utility class for mapping HTTP errors to user-friendly Spanish messages.
 * Used by services to translate backend API errors into user-friendly messages.
 * 
 * Implements Requirements 15.1-15.8: HTTP status code to message mapping
 */
public class ErrorMessageMapper {

    private ErrorMessageMapper() {
        // Utility class
    }

    /**
     * Maps HTTP status codes to user-friendly Spanish messages.
     * 
     * @param statusCode the HTTP status code
     * @return user-friendly error message
     */
    public static String mapStatusCodeToMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Los datos proporcionados son inválidos. Verifique los campos resaltados.";
            case 401 -> "Su sesión ha expirado. Por favor, inicie sesión nuevamente.";
            case 403 -> "No tiene permiso para acceder a este recurso.";
            case 404 -> "El recurso solicitado no existe.";
            case 409 -> "Existe un conflicto con un recurso existente. Esto puede deberse a un registro duplicado.";
            case 422 -> "No se puede procesar esta acción. Verifique las condiciones de negocio.";
            case 429 -> "Límite de cuota excedido, intente más tarde";
            case 500 -> "Error del servidor, intente más tarde";
            case 502 -> "El servidor backend no está disponible en este momento.";
            case 503 -> "El servicio no está disponible. Intente más tarde.";
            default -> "Ha ocurrido un error inesperado. Código: " + statusCode;
        };
    }

    /**
     * Maps common exception types to appropriate DrinkHouse exceptions.
     * 
     * @param ex the original exception
     * @return mapped DrinkHouse exception
     */
    public static DrinkHouseException mapException(Exception ex) {
        if (ex instanceof HttpClientErrorException clientEx) {
            return mapClientException(clientEx);
        } else if (ex instanceof HttpServerErrorException serverEx) {
            return mapServerException(serverEx);
        } else if (ex instanceof ResourceAccessException) {
            return new ExternalServiceException("Backend API", null, 
                "No se pudo conectar con el servidor, verifique la conexión");
        } else if (ex instanceof DrinkHouseException drinkHouseEx) {
            return drinkHouseEx;
        } else {
            return new DrinkHouseException("Error inesperado: " + ex.getMessage(), ex);
        }
    }

    /**
     * Maps HTTP 4xx client errors to specific exception types.
     */
    private static DrinkHouseException mapClientException(HttpClientErrorException ex) {
        int statusCode = ex.getStatusCode().value();
        String responseBody = ex.getResponseBodyAsString();
        String extractedMessage = extractMessageFromResponseBody(responseBody);
        
        return switch (statusCode) {
            case 400 -> new ValidationException(
                extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(400));
            case 401 -> new DrinkHouseException("Unauthorized") {
                // Special marker for unauthorized to trigger redirect
            };
            case 403 -> new DrinkHouseException("Forbidden") {
                // Special marker for forbidden to show access denied page
            };
            case 404 -> new EntityNotFoundException(
                extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(404));
            case 409 -> new ConflictException(
                extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(409));
            case 422 -> new BusinessRuleException(
                extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(422));
            case 429 -> new DrinkHouseException(mapStatusCodeToMessage(429));
            default -> new ExternalServiceException("Backend API", statusCode, 
                extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(statusCode));
        };
    }

    /**
     * Maps HTTP 5xx server errors to external service exceptions.
     */
    private static DrinkHouseException mapServerException(HttpServerErrorException ex) {
        int statusCode = ex.getStatusCode().value();
        String responseBody = ex.getResponseBodyAsString();
        String extractedMessage = extractMessageFromResponseBody(responseBody);
        
        return new ExternalServiceException("Backend API", statusCode,
            extractedMessage != null ? extractedMessage : mapStatusCodeToMessage(statusCode));
    }

    /**
     * Extracts error message from JSON response body.
     * Tries to parse common error response formats from the backend.
     */
    private static String extractMessageFromResponseBody(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Try to extract message from common JSON error formats
            // Format 1: {"message": "error message"}
            if (responseBody.contains("\"message\"")) {
                int start = responseBody.indexOf("\"message\"");
                int colonPos = responseBody.indexOf(":", start);
                if (colonPos != -1) {
                    int quoteStart = responseBody.indexOf("\"", colonPos) + 1;
                    int quoteEnd = responseBody.indexOf("\"", quoteStart);
                    if (quoteStart > 0 && quoteEnd > quoteStart) {
                        return responseBody.substring(quoteStart, quoteEnd);
                    }
                }
            }
            
            // Format 2: {"error": "error message"}
            if (responseBody.contains("\"error\"")) {
                int start = responseBody.indexOf("\"error\"");
                int colonPos = responseBody.indexOf(":", start);
                if (colonPos != -1) {
                    int quoteStart = responseBody.indexOf("\"", colonPos) + 1;
                    int quoteEnd = responseBody.indexOf("\"", quoteStart);
                    if (quoteStart > 0 && quoteEnd > quoteStart) {
                        return responseBody.substring(quoteStart, quoteEnd);
                    }
                }
            }
            
            // If it's a simple string without JSON structure
            if (!responseBody.trim().startsWith("{") && responseBody.length() < 200) {
                return responseBody.trim();
            }
        } catch (Exception e) {
            // If parsing fails, return null to use default message
        }
        
        return null;
    }

    /**
     * Creates a user-friendly message for specific business scenarios.
     * These map to common backend validation messages.
     */
    public static class BusinessMessages {
        public static final String EMAIL_DUPLICADO = "Email ya registrado";
        public static final String RUC_DUPLICADO = "RUC ya registrado";
        public static final String NOMBRE_DUPLICADO = "Nombre duplicado";
        public static final String CATEGORIA_CON_PRODUCTOS = "No se puede eliminar categoría con productos asociados";
        public static final String STOCK_INSUFICIENTE = "Stock insuficiente para completar la operación";
        public static final String CANTIDAD_MAYOR_DISPONIBLE = "La cantidad solicitada es mayor a la disponible";
        public static final String CUOTA_IA_AGOTADA = "Cuota de IA agotada para este mes";
        public static final String FORMATO_IMAGEN_NO_SOPORTADO = "Formato de imagen no soportado";
        public static final String SESION_EXPIRADA = "Sesión expirada";
        
        private BusinessMessages() {
            // Utility class
        }
    }

    /**
     * Gets specific business message based on error content.
     * 
     * @param originalMessage the original error message
     * @return specific business message or original message
     */
    public static String getBusinessMessage(String originalMessage) {
        if (originalMessage == null) {
            return null;
        }
        
        String lowerMessage = originalMessage.toLowerCase();
        
        if (lowerMessage.contains("email") && lowerMessage.contains("duplicate")) {
            return BusinessMessages.EMAIL_DUPLICADO;
        } else if (lowerMessage.contains("ruc") && lowerMessage.contains("duplicate")) {
            return BusinessMessages.RUC_DUPLICADO;
        } else if (lowerMessage.contains("nombre") && lowerMessage.contains("duplicate")) {
            return BusinessMessages.NOMBRE_DUPLICADO;
        } else if (lowerMessage.contains("categoria") && lowerMessage.contains("productos")) {
            return BusinessMessages.CATEGORIA_CON_PRODUCTOS;
        } else if (lowerMessage.contains("stock") && lowerMessage.contains("insuficiente")) {
            return BusinessMessages.STOCK_INSUFICIENTE;
        } else if (lowerMessage.contains("cantidad") && lowerMessage.contains("mayor")) {
            return BusinessMessages.CANTIDAD_MAYOR_DISPONIBLE;
        } else if (lowerMessage.contains("cuota") && lowerMessage.contains("ia")) {
            return BusinessMessages.CUOTA_IA_AGOTADA;
        } else if (lowerMessage.contains("formato") && lowerMessage.contains("imagen")) {
            return BusinessMessages.FORMATO_IMAGEN_NO_SOPORTADO;
        } else if (lowerMessage.contains("sesion") || lowerMessage.contains("session")) {
            return BusinessMessages.SESION_EXPIRADA;
        }
        
        return originalMessage;
    }
}