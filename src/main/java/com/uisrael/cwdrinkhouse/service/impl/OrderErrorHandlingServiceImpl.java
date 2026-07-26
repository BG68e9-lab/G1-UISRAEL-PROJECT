package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.UserFriendlyError;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.OrderErrorHandlingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of OrderErrorHandlingService for comprehensive error handling and transformation.
 * 
 * This service transforms technical exceptions into user-friendly error messages while preserving
 * diagnostic information for support teams. It provides categorization, correlation tracking,
 * and retry guidance for different types of failures.
 * 
 * Key features:
 * - User-friendly error transformation for WebClient exceptions
 * - Field-level validation error handling
 * - Database constraint violation processing
 * - Timeout handling with retry guidance
 * - Detailed error logging with correlation IDs
 * - Error categorization for HTTP status mapping
 * 
 * Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6
 */
@Service
public class OrderErrorHandlingServiceImpl implements OrderErrorHandlingService {

    private static final Logger logger = LoggerFactory.getLogger(OrderErrorHandlingServiceImpl.class);

    @Value("${app.support.email:soporte@cwdrinkhouse.com}")
    private String supportEmail;

    @Value("${app.support.phone:+593-2-123-4567}")
    private String supportPhone;

    @Value("${app.support.ticket-url:https://soporte.cwdrinkhouse.com}")
    private String supportTicketUrl;

    @Value("${app.error.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${app.error.retry.delay-millis:2000}")
    private long retryDelayMillis;

    // Common error message patterns
    private static final String PROVIDER_NOT_FOUND_MSG = "El proveedor especificado no existe o no está disponible";
    private static final String PRODUCT_NOT_FOUND_MSG = "Uno o más productos especificados no existen o no están disponibles";
    private static final String BACKEND_UNAVAILABLE_MSG = "El servicio no está disponible temporalmente";
    private static final String VALIDATION_ERROR_MSG = "Los datos ingresados contienen errores que deben corregirse";
    private static final String DATABASE_ERROR_MSG = "Error al procesar la información en la base de datos";
    private static final String TIMEOUT_ERROR_MSG = "La operación tardó más tiempo del esperado";
    private static final String GENERIC_ERROR_MSG = "Ha ocurrido un error inesperado. Por favor, intente nuevamente";

    @Override
    public UserFriendlyError handleWebClientException(WebClientResponseException ex) {
        String correlationId = generateCorrelationId();
        logDetailedError(ex, "WebClient API call failed");

        String userMessage = generateWebClientUserMessage(ex);
        String technicalMessage = String.format("WebClient error: %s %s - %s", 
                ex.getStatusCode(), ex.getStatusText(), ex.getResponseBodyAsString());

        UserFriendlyError error = new UserFriendlyError(
                userMessage, 
                technicalMessage, 
                correlationId,
                categorizeWebClientException(ex),
                ex.getStatusCode().value()
        );

        addWebClientSuggestedActions(error, ex);
        addRetryInformation(error, ex);
        addSupportInformation(error);

        return error;
    }

    @Override
    public UserFriendlyError handleValidationException(ValidationException ex) {
        String correlationId = generateCorrelationId();
        logDetailedError(ex, "Order validation failed");

        String userMessage = VALIDATION_ERROR_MSG;
        String technicalMessage = "Validation failed: " + ex.getMessage();

        UserFriendlyError error = new UserFriendlyError(
                userMessage,
                technicalMessage,
                correlationId,
                UserFriendlyError.ErrorCategory.VALIDATION_ERROR,
                HttpStatus.BAD_REQUEST.value()
        );

        processValidationFieldErrors(error, ex);
        addValidationSuggestedActions(error, ex);
        addSupportInformation(error);

        return error;
    }

    @Override
    public UserFriendlyError handleDatabaseException(DataAccessException ex) {
        String correlationId = generateCorrelationId();
        logDetailedError(ex, "Database operation failed");

        String userMessage = generateDatabaseUserMessage(ex);
        String technicalMessage = "Database error: " + ex.getMessage();

        UserFriendlyError error = new UserFriendlyError(
                userMessage,
                technicalMessage,
                correlationId,
                UserFriendlyError.ErrorCategory.DATABASE_ERROR,
                determineDatabaseHttpStatus(ex)
        );

        addDatabaseSuggestedActions(error, ex);
        addRetryInformation(error, ex);
        addSupportInformation(error);

        return error;
    }

    @Override
    public UserFriendlyError handleTimeoutException(TimeoutException ex) {
        String correlationId = generateCorrelationId();
        logDetailedError(ex, "Operation timeout occurred");

        String userMessage = TIMEOUT_ERROR_MSG;
        String technicalMessage = "Timeout occurred: " + ex.getMessage();

        UserFriendlyError error = new UserFriendlyError(
                userMessage,
                technicalMessage,
                correlationId,
                UserFriendlyError.ErrorCategory.TIMEOUT_ERROR,
                HttpStatus.REQUEST_TIMEOUT.value()
        );

        addTimeoutSuggestedActions(error);
        addTimeoutRetryInformation(error);
        addSupportInformation(error);

        return error;
    }

    @Override
    public UserFriendlyError handleGenericException(Exception ex, String operationContext) {
        String correlationId = generateCorrelationId();
        logDetailedError(ex, operationContext != null ? operationContext : "Generic operation failed");

        String userMessage = GENERIC_ERROR_MSG;
        String technicalMessage = String.format("Unexpected error during %s: %s", 
                operationContext, ex.getMessage());

        UserFriendlyError error = new UserFriendlyError(
                userMessage,
                technicalMessage,
                correlationId,
                UserFriendlyError.ErrorCategory.SYSTEM_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        addGenericSuggestedActions(error);
        addSupportInformation(error);

        return error;
    }

    @Override
    public String logDetailedError(Exception ex, String operationContext) {
        String correlationId = generateCorrelationId();
        
        logger.error("Error occurred during {}: [CorrelationId: {}] {} - {}",
                operationContext, correlationId, ex.getClass().getSimpleName(), ex.getMessage(), ex);

        // Log additional context if available
        if (ex instanceof WebClientResponseException) {
            WebClientResponseException wcEx = (WebClientResponseException) ex;
            logger.error("WebClient error details [CorrelationId: {}]: Status={}, Method={}, URL={}, ResponseBody={}",
                    correlationId, wcEx.getStatusCode(), wcEx.getRequest().getMethod(), 
                    wcEx.getRequest().getURI(), wcEx.getResponseBodyAsString());
        }

        return correlationId;
    }

    @Override
    public String generateCorrelationId() {
        return "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    @Override
    public UserFriendlyError.ErrorCategory categorizeException(Exception ex) {
        if (ex instanceof ValidationException) {
            return UserFriendlyError.ErrorCategory.VALIDATION_ERROR;
        } else if (ex instanceof WebClientResponseException) {
            return categorizeWebClientException((WebClientResponseException) ex);
        } else if (ex instanceof DataAccessException) {
            return UserFriendlyError.ErrorCategory.DATABASE_ERROR;
        } else if (ex instanceof TimeoutException || ex instanceof QueryTimeoutException) {
            return UserFriendlyError.ErrorCategory.TIMEOUT_ERROR;
        } else {
            return UserFriendlyError.ErrorCategory.SYSTEM_ERROR;
        }
    }

    @Override
    public int determineHttpStatusCode(Exception ex) {
        if (ex instanceof ValidationException) {
            return HttpStatus.BAD_REQUEST.value();
        } else if (ex instanceof WebClientResponseException) {
            return ((WebClientResponseException) ex).getStatusCode().value();
        } else if (ex instanceof DataAccessException) {
            return determineDatabaseHttpStatus((DataAccessException) ex);
        } else if (ex instanceof TimeoutException) {
            return HttpStatus.REQUEST_TIMEOUT.value();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }

    @Override
    public UserFriendlyError addRetryInformation(UserFriendlyError error, Exception ex) {
        if (isRetryableException(ex)) {
            UserFriendlyError.RetryInformation retryInfo = new UserFriendlyError.RetryInformation(
                    true, maxRetryAttempts, retryDelayMillis);
            retryInfo.setRetryStrategy("exponential_backoff");
            retryInfo.setNextRetryTime(LocalDateTime.now().plusSeconds(retryDelayMillis / 1000));
            error.setRetryInformation(retryInfo);
        }
        return error;
    }

    @Override
    public UserFriendlyError addSupportInformation(UserFriendlyError error) {
        UserFriendlyError.SupportInformation supportInfo = new UserFriendlyError.SupportInformation(
                supportEmail, supportPhone, supportTicketUrl);
        supportInfo.setDocumentationUrl("https://docs.cwdrinkhouse.com/troubleshooting");
        supportInfo.setTroubleshootingGuide("https://docs.cwdrinkhouse.com/orders/troubleshooting");
        error.setSupportInformation(supportInfo);
        return error;
    }

    // Private helper methods

    private String generateWebClientUserMessage(WebClientResponseException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        
        if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
            return VALIDATION_ERROR_MSG;
        } else if (statusCode.value() == HttpStatus.NOT_FOUND.value()) {
            if (ex.getResponseBodyAsString().toLowerCase().contains("proveedor")) {
                return PROVIDER_NOT_FOUND_MSG;
            } else if (ex.getResponseBodyAsString().toLowerCase().contains("producto")) {
                return PRODUCT_NOT_FOUND_MSG;
            }
            return "El recurso solicitado no fue encontrado";
        } else if (statusCode.value() == HttpStatus.CONFLICT.value()) {
            return "Existe un conflicto con los datos. Verifique que no haya duplicados";
        } else if (statusCode.value() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
            return "Los datos no cumplen con las reglas de negocio establecidas";
        } else if (statusCode.value() == HttpStatus.SERVICE_UNAVAILABLE.value() ||
                   statusCode.value() == HttpStatus.BAD_GATEWAY.value() ||
                   statusCode.value() == HttpStatus.GATEWAY_TIMEOUT.value()) {
            return BACKEND_UNAVAILABLE_MSG;
        } else {
            return GENERIC_ERROR_MSG;
        }
    }

    private UserFriendlyError.ErrorCategory categorizeWebClientException(WebClientResponseException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        
        if (statusCode.is4xxClientError()) {
            if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
                return UserFriendlyError.ErrorCategory.VALIDATION_ERROR;
            } else if (statusCode.value() == HttpStatus.UNAUTHORIZED.value()) {
                return UserFriendlyError.ErrorCategory.AUTHENTICATION_ERROR;
            } else if (statusCode.value() == HttpStatus.FORBIDDEN.value()) {
                return UserFriendlyError.ErrorCategory.AUTHORIZATION_ERROR;
            } else if (statusCode.value() == HttpStatus.NOT_FOUND.value()) {
                return UserFriendlyError.ErrorCategory.RESOURCE_NOT_FOUND;
            } else if (statusCode.value() == HttpStatus.CONFLICT.value() ||
                       statusCode.value() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
                return UserFriendlyError.ErrorCategory.BUSINESS_RULE_VIOLATION;
            } else {
                return UserFriendlyError.ErrorCategory.VALIDATION_ERROR;
            }
        } else if (statusCode.is5xxServerError()) {
            if (statusCode.value() == HttpStatus.SERVICE_UNAVAILABLE.value() ||
                statusCode.value() == HttpStatus.BAD_GATEWAY.value() ||
                statusCode.value() == HttpStatus.GATEWAY_TIMEOUT.value()) {
                return UserFriendlyError.ErrorCategory.SERVICE_UNAVAILABLE;
            } else {
                return UserFriendlyError.ErrorCategory.SYSTEM_ERROR;
            }
        }
        
        return UserFriendlyError.ErrorCategory.UNKNOWN;
    }

    private void addWebClientSuggestedActions(UserFriendlyError error, WebClientResponseException ex) {
        HttpStatusCode statusCode = ex.getStatusCode();
        
        if (statusCode.value() == HttpStatus.BAD_REQUEST.value()) {
            error.addSuggestedAction("Verifique que todos los campos requeridos estén completos");
            error.addSuggestedAction("Asegúrese de que los datos ingresados sean válidos");
        } else if (statusCode.value() == HttpStatus.NOT_FOUND.value()) {
            error.addSuggestedAction("Verifique que el proveedor seleccionado existe");
            error.addSuggestedAction("Confirme que todos los productos están disponibles");
        } else if (statusCode.value() == HttpStatus.CONFLICT.value()) {
            error.addSuggestedAction("Revise si ya existe una orden similar");
            error.addSuggestedAction("Verifique los códigos de referencia únicos");
        } else if (statusCode.value() == HttpStatus.UNPROCESSABLE_ENTITY.value()) {
            error.addSuggestedAction("Revise las reglas de negocio para la creación de órdenes");
            error.addSuggestedAction("Verifique los estados y transiciones permitidas");
        } else if (statusCode.value() == HttpStatus.SERVICE_UNAVAILABLE.value() ||
                   statusCode.value() == HttpStatus.BAD_GATEWAY.value() ||
                   statusCode.value() == HttpStatus.GATEWAY_TIMEOUT.value()) {
            error.addSuggestedAction("Intente nuevamente en unos momentos");
            error.addSuggestedAction("Verifique su conexión a internet");
            error.addSuggestedAction("Contacte al soporte si el problema persiste");
        } else {
            error.addSuggestedAction("Intente nuevamente");
            error.addSuggestedAction("Contacte al soporte técnico si el problema persiste");
        }
    }

    private void processValidationFieldErrors(UserFriendlyError error, ValidationException ex) {
        String message = ex.getMessage();
        
        // Parse common validation patterns and map to field errors
        if (message.contains("proveedor")) {
            error.addFieldError("proveedorId", "Debe seleccionar un proveedor válido");
        }
        if (message.contains("detalle") || message.contains("línea")) {
            error.addFieldError("detalles", "Los detalles de la orden contienen errores");
        }
        if (message.contains("cantidad")) {
            error.addFieldError("cantidad", "La cantidad debe ser mayor a cero");
        }
        if (message.contains("precio")) {
            error.addFieldError("precioUnitario", "El precio unitario debe ser mayor a cero");
        }
        if (message.contains("producto")) {
            error.addFieldError("productoId", "Debe seleccionar productos válidos");
        }
    }

    private void addValidationSuggestedActions(UserFriendlyError error, ValidationException ex) {
        error.addSuggestedAction("Revise los campos marcados en rojo");
        error.addSuggestedAction("Complete todos los campos obligatorios");
        error.addSuggestedAction("Verifique que las cantidades y precios sean positivos");
        
        if (ex.getMessage().contains("proveedor")) {
            error.addSuggestedAction("Seleccione un proveedor de la lista disponible");
        }
        if (ex.getMessage().contains("producto")) {
            error.addSuggestedAction("Verifique que todos los productos estén disponibles");
        }
    }

    private String generateDatabaseUserMessage(DataAccessException ex) {
        if (ex instanceof DataIntegrityViolationException) {
            String message = ex.getMessage();
            if (message.contains("foreign key") || message.contains("FOREIGN KEY")) {
                if (message.toLowerCase().contains("proveedor")) {
                    return "El proveedor seleccionado no existe en el sistema";
                } else if (message.toLowerCase().contains("producto")) {
                    return "Uno o más productos seleccionados no existen en el sistema";
                }
                return "Hay referencias a datos que no existen en el sistema";
            } else if (message.contains("unique") || message.contains("UNIQUE")) {
                return "Ya existe una orden con el mismo código de referencia";
            }
            return "Los datos violan las reglas de integridad de la base de datos";
        } else if (ex instanceof QueryTimeoutException) {
            return "La consulta a la base de datos tardó más tiempo del esperado";
        }
        
        return DATABASE_ERROR_MSG;
    }

    private int determineDatabaseHttpStatus(DataAccessException ex) {
        if (ex instanceof DataIntegrityViolationException) {
            String message = ex.getMessage();
            if (message.contains("foreign key") || message.contains("FOREIGN KEY")) {
                return HttpStatus.BAD_REQUEST.value();
            } else if (message.contains("unique") || message.contains("UNIQUE")) {
                return HttpStatus.CONFLICT.value();
            }
            return HttpStatus.UNPROCESSABLE_ENTITY.value();
        } else if (ex instanceof QueryTimeoutException) {
            return HttpStatus.REQUEST_TIMEOUT.value();
        }
        
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    private void addDatabaseSuggestedActions(UserFriendlyError error, DataAccessException ex) {
        if (ex instanceof DataIntegrityViolationException) {
            String message = ex.getMessage();
            if (message.contains("foreign key") || message.contains("FOREIGN KEY")) {
                error.addSuggestedAction("Verifique que el proveedor seleccionado existe");
                error.addSuggestedAction("Confirme que todos los productos están disponibles");
            } else if (message.contains("unique") || message.contains("UNIQUE")) {
                error.addSuggestedAction("Use un código de referencia diferente");
                error.addSuggestedAction("Verifique si la orden ya fue creada anteriormente");
            }
        } else if (ex instanceof QueryTimeoutException) {
            error.addSuggestedAction("Intente nuevamente en unos momentos");
            error.addSuggestedAction("Simplifique la orden si es muy compleja");
        }
        
        error.addSuggestedAction("Contacte al soporte si el problema persiste");
    }

    private void addTimeoutSuggestedActions(UserFriendlyError error) {
        error.addSuggestedAction("Intente nuevamente en unos momentos");
        error.addSuggestedAction("Verifique su conexión a internet");
        error.addSuggestedAction("Reduzca la cantidad de elementos si la orden es muy grande");
        error.addSuggestedAction("Contacte al soporte si el problema continúa");
    }

    private void addTimeoutRetryInformation(UserFriendlyError error) {
        UserFriendlyError.RetryInformation retryInfo = new UserFriendlyError.RetryInformation(
                true, maxRetryAttempts, retryDelayMillis * 2); // Double delay for timeouts
        retryInfo.setRetryStrategy("exponential_backoff_with_longer_delay");
        retryInfo.setNextRetryTime(LocalDateTime.now().plusSeconds((retryDelayMillis * 2) / 1000));
        error.setRetryInformation(retryInfo);
    }

    private void addGenericSuggestedActions(UserFriendlyError error) {
        error.addSuggestedAction("Intente nuevamente");
        error.addSuggestedAction("Verifique que todos los datos sean correctos");
        error.addSuggestedAction("Contacte al soporte técnico con el código de error");
    }

    private boolean isRetryableException(Exception ex) {
        if (ex instanceof TimeoutException || ex instanceof QueryTimeoutException) {
            return true;
        }
        
        if (ex instanceof WebClientResponseException) {
            HttpStatusCode statusCode = ((WebClientResponseException) ex).getStatusCode();
            return statusCode.value() == HttpStatus.SERVICE_UNAVAILABLE.value() ||
                   statusCode.value() == HttpStatus.BAD_GATEWAY.value() ||
                   statusCode.value() == HttpStatus.GATEWAY_TIMEOUT.value() ||
                   statusCode.value() == HttpStatus.REQUEST_TIMEOUT.value();
        }
        
        // Database connection issues are typically retryable
        if (ex instanceof DataAccessException) {
            String message = ex.getMessage();
            return message.contains("connection") || 
                   message.contains("timeout") || 
                   message.contains("unable to obtain");
        }
        
        return false;
    }
}