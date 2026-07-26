package com.uisrael.cwdrinkhouse.exception;

import com.uisrael.cwdrinkhouse.configuration.ErrorHandlingFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Web UI specific exception handler for Thymeleaf-based controllers.
 * Handles exceptions and directs users to appropriate error pages or shows notifications.
 * 
 * Implements Requirements 15.1-15.8: Error handling and user feedback
 */
@ControllerAdvice(basePackages = "com.uisrael.cwdrinkhouse.controller")
public class WebUIExceptionHandler {

    /**
     * Handles HTTP 400 Bad Request errors.
     * Shows user-friendly notification with problematic fields highlighted.
     * Requirement 15.1
     */
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public String handleBadRequest(Exception ex, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        
        String errorMessage = "Los datos proporcionados son inválidos. Verifique los campos resaltados.";
        String errorDetails = extractValidationErrorDetails(ex);
        
        addErrorNotification(redirectAttributes, errorMessage, errorDetails);
        
        // If it's a form submission, return to the form page
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + extractPathFromUrl(referer);
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Handles HTTP 401 Unauthorized errors.
     * Redirects to login page.
     * Requirement 15.2
     */
    @ExceptionHandler(HttpClientErrorException.Unauthorized.class)
    public String handleUnauthorized(RedirectAttributes redirectAttributes) {
        addErrorNotification(redirectAttributes, "Su sesión ha expirado. Por favor, inicie sesión nuevamente.", "");
        return "redirect:/login";
    }

    /**
     * Handles HTTP 403 Forbidden errors.
     * Shows access denied page.
     * Requirement 15.3
     */
    @ExceptionHandler(HttpClientErrorException.Forbidden.class)
    public String handleForbidden(Model model) {
        model.addAttribute("errorTitle", "Acceso Denegado");
        model.addAttribute("errorMessage", "No tiene permiso para acceder a este recurso.");
        model.addAttribute("errorCode", "403");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "error/403";
    }

    /**
     * Handles HTTP 404 Not Found errors.
     * Shows resource not found page.
     * Requirement 15.4
     */
    @ExceptionHandler({EntityNotFoundException.class, HttpClientErrorException.NotFound.class})
    public String handleNotFound(Exception ex, Model model) {
        String message = ex.getMessage() != null ? ex.getMessage() : "El recurso solicitado no existe.";
        
        model.addAttribute("errorTitle", "Recurso No Encontrado");
        model.addAttribute("errorMessage", message);
        model.addAttribute("errorCode", "404");
        model.addAttribute("timestamp", LocalDateTime.now());
        return "error/404";
    }

    /**
     * Handles HTTP 409 Conflict errors.
     * Shows notification with explanatory message.
     * Requirement 15.5
     */
    @ExceptionHandler({ConflictException.class, HttpClientErrorException.Conflict.class})
    public String handleConflict(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        String errorMessage = extractConflictMessage(ex.getMessage());
        
        addErrorNotification(redirectAttributes, errorMessage, "");
        
        // Redirect back to the referring page or dashboard
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + extractPathFromUrl(referer);
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Handles HTTP 422 Unprocessable Entity errors.
     * Shows notification with validation explanation.
     * Requirement 15.6
     */
    @ExceptionHandler({BusinessRuleException.class, HttpClientErrorException.UnprocessableEntity.class})
    public String handleUnprocessableEntity(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        String errorMessage = extractBusinessRuleMessage(ex.getMessage());
        
        addErrorNotification(redirectAttributes, errorMessage, "");
        
        // Redirect back to the referring page or dashboard
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + extractPathFromUrl(referer);
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Handles HTTP 429 Too Many Requests errors.
     * Shows rate limit exceeded notification.
     * Requirement 15.7
     */
    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public String handleTooManyRequests(RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        addErrorNotification(redirectAttributes, "Límite de cuota excedido, intente más tarde", "");
        
        // Redirect back to the referring page or dashboard
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + extractPathFromUrl(referer);
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Handles HTTP 500 Internal Server Error.
     * Shows server error notification.
     * Requirement 15.8
     */
    @ExceptionHandler({HttpServerErrorException.class, DrinkHouseException.class})
    public String handleServerError(Exception ex, Model model) {
        
        // For 500 errors, show the error page instead of redirect
        model.addAttribute("errorTitle", "Error del Servidor");
        model.addAttribute("errorMessage", "Error del servidor, intente más tarde");
        model.addAttribute("errorCode", "500");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("errorDetails", ex.getClass().getSimpleName());
        
        return "error/500";
    }

    /**
     * Handles connection timeouts and network issues.
     * Shows connection error notification.
     * Requirement 15.9
     */
    @ExceptionHandler({
        ResourceAccessException.class,
        ErrorHandlingFilter.ConnectionRefusedException.class,
        ErrorHandlingFilter.ConnectionTimeoutException.class,
        ErrorHandlingFilter.ConnectionInterruptedException.class
    })
    public String handleConnectionError(Exception ex, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        
        String errorMessage = getConnectionErrorMessage(ex);
        String errorDetails = getConnectionErrorDetails(ex);
        
        addErrorNotification(redirectAttributes, errorMessage, errorDetails);
        
        // Redirect back to the referring page or dashboard
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + extractPathFromUrl(referer);
        }
        
        return "redirect:/dashboard";
    }

    /**
     * Handles any other uncaught exceptions.
     * Shows generic error page.
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericError(Exception ex, Model model) {
        
        model.addAttribute("errorTitle", "Error Inesperado");
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, contacte al soporte si el problema persiste.");
        model.addAttribute("errorCode", "500");
        model.addAttribute("timestamp", LocalDateTime.now());
        model.addAttribute("errorDetails", ex.getClass().getSimpleName() + ": " + ex.getMessage());
        
        return "error/500";
    }

    // Helper methods

    /**
     * Adds error notification to redirect attributes.
     * Notifications disappear automatically after 5 seconds (Requirement 15.10).
     */
    private void addErrorNotification(RedirectAttributes redirectAttributes, String message, String details) {
        redirectAttributes.addFlashAttribute("notificationType", "error");
        redirectAttributes.addFlashAttribute("notificationMessage", message);
        redirectAttributes.addFlashAttribute("notificationDetails", details);
        redirectAttributes.addFlashAttribute("notificationTimeout", 5000); // 5 seconds
    }

    /**
     * Adds success notification to redirect attributes.
     * Success notifications show briefly (3 seconds) (Requirement 15.11).
     */
    public static void addSuccessNotification(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("notificationType", "success");
        redirectAttributes.addFlashAttribute("notificationMessage", message);
        redirectAttributes.addFlashAttribute("notificationTimeout", 3000); // 3 seconds
    }

    /**
     * Adds info notification to redirect attributes.
     */
    public static void addInfoNotification(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("notificationType", "info");
        redirectAttributes.addFlashAttribute("notificationMessage", message);
        redirectAttributes.addFlashAttribute("notificationTimeout", 4000); // 4 seconds
    }

    /**
     * Extracts validation error details from various exception types.
     */
    private String extractValidationErrorDetails(Exception ex) {
        if (ex instanceof MethodArgumentNotValidException methodEx) {
            StringBuilder details = new StringBuilder();
            methodEx.getBindingResult().getFieldErrors().forEach(error -> {
                details.append(error.getField())
                       .append(": ")
                       .append(error.getDefaultMessage())
                       .append("; ");
            });
            return details.toString();
        } else if (ex instanceof ConstraintViolationException constraintEx) {
            StringBuilder details = new StringBuilder();
            constraintEx.getConstraintViolations().forEach(violation -> {
                details.append(violation.getPropertyPath())
                       .append(": ")
                       .append(violation.getMessage())
                       .append("; ");
            });
            return details.toString();
        }
        return ex.getMessage() != null ? ex.getMessage() : "";
    }

    /**
     * Extracts user-friendly conflict message.
     */
    private String extractConflictMessage(String originalMessage) {
        if (originalMessage == null) {
            return "Existe un conflicto con un recurso existente";
        }
        
        // Map common conflict messages to Spanish
        if (originalMessage.toLowerCase().contains("email")) {
            return "Email ya registrado";
        } else if (originalMessage.toLowerCase().contains("ruc")) {
            return "RUC ya registrado";
        } else if (originalMessage.toLowerCase().contains("nombre")) {
            return "Nombre duplicado";
        } else if (originalMessage.toLowerCase().contains("codigo")) {
            return "Código duplicado";
        }
        
        return originalMessage;
    }

    /**
     * Extracts user-friendly business rule message.
     */
    private String extractBusinessRuleMessage(String originalMessage) {
        if (originalMessage == null) {
            return "No se puede procesar esta acción. Verifique las condiciones de negocio.";
        }
        
        // Map common business rule messages to Spanish
        if (originalMessage.toLowerCase().contains("productos asociados")) {
            return "No se puede eliminar categoría con productos asociados";
        } else if (originalMessage.toLowerCase().contains("stock insuficiente")) {
            return "Stock insuficiente para completar la operación";
        } else if (originalMessage.toLowerCase().contains("cantidad mayor")) {
            return "La cantidad solicitada es mayor a la disponible";
        }
        
        return originalMessage;
    }

    /**
     * Extracts path from full URL for redirection.
     */
    private String extractPathFromUrl(String url) {
        try {
            return url.substring(url.indexOf("/", url.indexOf("://") + 3));
        } catch (Exception e) {
            return "/dashboard";
        }
    }

    /**
     * Gets appropriate error message based on connection exception type.
     */
    private String getConnectionErrorMessage(Exception ex) {
        if (ex instanceof ErrorHandlingFilter.ConnectionRefusedException) {
            return "El servicio backend no está disponible. Contacte al administrador.";
        } else if (ex instanceof ErrorHandlingFilter.ConnectionTimeoutException) {
            return "La conexión al servidor tardó demasiado tiempo. Intente nuevamente.";
        } else if (ex instanceof ErrorHandlingFilter.ConnectionInterruptedException) {
            return "La conexión se perdió inesperadamente. Verifique su red e intente de nuevo.";
        } else {
            return "No se pudo conectar con el servidor, verifique la conexión";
        }
    }

    /**
     * Gets additional error details for connection exceptions.
     */
    private String getConnectionErrorDetails(Exception ex) {
        if (ex instanceof ErrorHandlingFilter.ConnectionRefusedException) {
            return "El servidor backend puede estar apagado o no responde.";
        } else if (ex instanceof ErrorHandlingFilter.ConnectionTimeoutException) {
            return "El servidor tardó más de lo esperado en responder.";
        } else if (ex instanceof ErrorHandlingFilter.ConnectionInterruptedException) {
            return "La conexión se cerró mientras se procesaba la solicitud.";
        } else {
            return "";
        }
    }
}