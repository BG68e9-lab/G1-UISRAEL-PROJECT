package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.OrderDetailDTO;
import com.uisrael.cwdrinkhouse.dto.DiagnosticReport;
import com.uisrael.cwdrinkhouse.dto.ValidationResult;
import com.uisrael.cwdrinkhouse.dto.UserFriendlyError;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.OrderService;
import com.uisrael.cwdrinkhouse.service.OrderDiagnosticsService;
import com.uisrael.cwdrinkhouse.service.OrderValidationService;
import com.uisrael.cwdrinkhouse.service.OrderErrorHandlingService;
import com.uisrael.cwdrinkhouse.service.TransactionManagementService;
import com.uisrael.cwdrinkhouse.service.DatabaseConstraintValidationService;
import com.uisrael.cwdrinkhouse.service.logging.OrderLoggingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service implementation for Order management operations.
 * Provides CRUD operations and state transition management for purchase orders.
 * Communicates with the backend REST API using WebClient.
 * 
 * Requirements: 5.1-5.14, 18.5-18.6
 */
/**
 * Service implementation for Order management operations.
 * Provides CRUD operations and state transition management for purchase orders.
 * Communicates with the backend REST API using WebClient.
 * 
 * Enhanced with:
 * - Robust error handling with specific error categories
 * - Comprehensive validation before processing
 * - Transaction boundaries for atomic operations
 * - Correlation ID generation for request tracking
 * - Integration with OrderValidationService and OrderErrorHandlingService
 * 
 * Requirements: 2.1, 2.2, 2.3, 2.4, 8.1, 8.2, 5.1-5.14, 18.5-18.6
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    
    // MDC key for correlation IDs in logs
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Autowired
    private WebClient webClient;

    @Autowired
    private Retry webClientRetry;
    
    @Autowired
    private OrderDiagnosticsService diagnosticsService;
    
    @Autowired
    private OrderValidationService validationService;
    
    @Autowired
    private OrderErrorHandlingService errorHandlingService;
    
    @Autowired
    private OrderLoggingService loggingService;

    @Autowired
    private TransactionManagementService transactionManagementService;

    @Autowired
    private DatabaseConstraintValidationService constraintValidationService;

    @Value("${app.backend.api.base-path:/api}")
    private String apiBasePath;

    private static final String ORDERS_ENDPOINT = "/ordenes-compra";

    /**
     * Valid state transitions according to the business rules.
     * BORRADOR → ENVIADA, ANULADA
     * ENVIADA → RECIBIDA, ANULADA
     * RECIBIDA, ANULADA → (final states)
     */
    private static final Map<String, List<String>> VALID_TRANSITIONS = Map.of(
            "BORRADOR", List.of("ENVIADA", "ANULADA"),
            "ENVIADA", List.of("RECIBIDA", "ANULADA"),
            "RECIBIDA", List.of(), // Final state
            "ANULADA", List.of()   // Final state
    );

    /**
     * Generates a unique correlation ID for request tracking across layers.
     * Stores the ID in MDC for inclusion in all logs for this request.
     * 
     * @return unique correlation ID
     */
    private String generateAndSetCorrelationId() {
        String correlationId = errorHandlingService.generateCorrelationId();
        MDC.put(CORRELATION_ID_KEY, correlationId);
        logger.debug("Generated correlation ID: {}", correlationId);
        return correlationId;
    }

    /**
     * Retrieves the current correlation ID from MDC or generates a new one.
     * 
     * @return correlation ID
     */
    private String getOrGenerateCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null) {
            correlationId = generateAndSetCorrelationId();
        }
        return correlationId;
    }

    /**
     * Resolves a UserFriendlyError by adding support and retry information.
     * 
     * @param error the error to resolve
     * @param exception the original exception
     * @return resolved error with complete information
     */
    private UserFriendlyError resolveErrorWithContext(UserFriendlyError error, Exception exception) {
        error = errorHandlingService.addRetryInformation(error, exception);
        error = errorHandlingService.addSupportInformation(error);
        return error;
    }

    /**
     * Clears MDC when operation is complete to prevent context leakage.
     */
    private void clearMDC() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    @Override
    public Page<OrderDTO> getAllOrders(int page, int size, String estado) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Getting orders page={}, size={}, estado={}", page, size, estado);

        Map<String, Object> params = new HashMap<>();
        params.put("page", page);
        params.put("size", size);
        if (estado != null && !estado.trim().isEmpty()) {
            params.put("estado", estado.trim());
        }

        String uri = buildUriWithParams(apiBasePath + ORDERS_ENDPOINT, params);

        try {
            // Try Page/object response first
            try {
                OrderPageResponse pageResponse = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(OrderPageResponse.class)
                        .retryWhen(webClientRetry)
                        .block();

                if (pageResponse != null && pageResponse.getContent() != null) {
                    logger.info("Retrieved {} orders (page {}, size {})",
                            pageResponse.getTotalElements(), page, size);
                    return convertToPage(pageResponse);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page for orders, converting: {}",
                        pageException.getMessage());
            }

            // Fallback: backend returns a plain JSON array
            List<OrderDTO> list = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(OrderDTO.class)
                    .retryWhen(webClientRetry)
                    .collectList()
                    .block();

            if (list != null) {
                int startIndex = page * size;
                int endIndex = Math.min(startIndex + size, list.size());
                List<OrderDTO> pageContent = startIndex < list.size()
                        ? list.subList(startIndex, endIndex)
                        : List.of();
                logger.info("Converted orders array to Page: {} items (total {})", pageContent.size(), list.size());
                return new PageImpl<>(pageContent,
                        org.springframework.data.domain.PageRequest.of(page, size), list.size());
            }

            return new PageImpl<>(List.of(),
                    org.springframework.data.domain.PageRequest.of(page, size), 0);

        } catch (WebClientResponseException ex) {
            logger.error("Error retrieving orders: {}", ex.getMessage());
            UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Retrieving all orders - page: " + page + ", size: " + size);
            throw mapWebClientException(ex, "Error al obtener las órdenes");
        } catch (Exception ex) {
            logger.error("Unexpected error getting orders", ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Retrieving all orders");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error retrieving orders");
            throw new RuntimeException("Error interno al obtener órdenes", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    public OrderDTO getOrderById(Long ordenCompraId) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Getting order by ID: {}", ordenCompraId);
        
        try {
            if (ordenCompraId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }

            return webClient.get()
                    .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}", ordenCompraId)
                    .retrieve()
                    .bodyToMono(OrderDTO.class)
                    .retryWhen(webClientRetry)
                    .block();
                    
        } catch (WebClientResponseException ex) {
            logger.error("Error retrieving order {}: {}", ordenCompraId, ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EntityNotFoundException("Orden", ordenCompraId.toString());
            }
            UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Getting order by ID: " + ordenCompraId);
            throw mapWebClientException(ex, "Error al obtener la orden");
        } catch (ValidationException vex) {
            logger.error("Validation error getting order {}: {}", ordenCompraId, vex.getMessage());
            throw vex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found: {}", ordenCompraId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error getting order {}", ordenCompraId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Getting order by ID");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error getting order: " + ordenCompraId);
            throw new RuntimeException("Error interno al obtener la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, timeout = 60)
    public OrderDTO createOrder(OrderDTO orderDTO) {
        String correlationId = generateAndSetCorrelationId();
        long operationStartTime = System.currentTimeMillis();
        
        logger.debug("Creating new order for provider: {} with correlationId: {}", 
            orderDTO != null ? orderDTO.getProveedorId() : null, correlationId);
        
        // Log order creation attempt with user context
        try {
            String userContext = "System"; // In a real app, get from authentication context
            loggingService.logOrderCreationAttempt(orderDTO, userContext, correlationId);
        } catch (Exception ex) {
            logger.warn("Failed to log order creation attempt", ex);
        }
        
        try {
            // Validate input
            if (orderDTO == null) {
                throw new ValidationException("Los datos de la orden son obligatorios");
            }

            // Use diagnostics service to validate payload structure
            try {
                diagnosticsService.validateOrderPayloadStructure(orderDTO);
            } catch (Exception ex) {
                logger.error("Payload validation failed: {}", ex.getMessage());
                loggingService.logSystemError(ex, "Payload structure validation", correlationId);
                throw new ValidationException("Estructura de datos inválida: " + ex.getMessage());
            }

            // Use validation service for comprehensive validation
            ValidationResult validationResult = validationService.validateOrderData(orderDTO);
            if (!validationResult.isValid()) {
                logger.warn("Order validation failed for provider {}", orderDTO.getProveedorId());
                
                // Log validation failures with field-specific details
                // loggingService.logValidationFailures(validationResult.getErrors(), correlationId);
                
                StringBuilder errorMessage = new StringBuilder("Validación fallida: ");
                validationResult.getErrors().forEach(error -> 
                    errorMessage.append(error.getMessage()).append("; ")
                );
                throw new ValidationException(errorMessage.toString());
            }

            // Ensure state is BORRADOR for new orders
            orderDTO.setEstado("BORRADOR");
            
            // Initialize version for optimistic locking
            orderDTO.setVersion(1L);
            
            // Set creation timestamp if missing
            if (orderDTO.getFechaCreacion() == null) {
                orderDTO.setFechaCreacion(LocalDateTime.now());
            }
            
            // Generate reference code if missing
            if (orderDTO.getCodigoReferencia() == null || orderDTO.getCodigoReferencia().trim().isEmpty()) {
                orderDTO.setCodigoReferencia(generateReferenceCode());
            }
            
            // Set negocio_id (business ID) - required by database schema
            if (orderDTO.getNegocioId() == null) {
                orderDTO.setNegocioId(1); // Default business ID
            }
            
            // Calculate total from details with performance timing
            long dbStartTime = System.currentTimeMillis();
            orderDTO = calculateOrderTotal(orderDTO);
            long dbDuration = System.currentTimeMillis() - dbStartTime;
            loggingService.logDatabaseOperation("calculateOrderTotal", Duration.ofMillis(dbDuration), correlationId);
            
            logger.debug("Order validation completed successfully for provider: {}", orderDTO.getProveedorId());

            // Validate database constraints before persistence (Requirement 8.4, 8.5)
            // This includes foreign key validation for provider and products, and unique constraint for reference codes
            try {
                logger.debug("Validating database constraints for order creation");
                ValidationResult constraintResult = constraintValidationService.validateOrder(orderDTO);
                
                if (!constraintResult.isValid()) {
                    logger.warn("Database constraint validation failed for provider {}", orderDTO.getProveedorId());
                    
                    // Log constraint violations
                    // loggingService.logValidationFailures(constraintResult.getErrors(), correlationId);
                    
                    StringBuilder constraintErrorMessage = new StringBuilder("Validación de integridad de datos fallida: ");
                    constraintResult.getErrors().forEach(error -> 
                        constraintErrorMessage.append(error.getMessage()).append("; ")
                    );
                    
                    logger.error("Constraint violation details: {}", constraintResult.getValidationContext());
                    throw new ValidationException(constraintErrorMessage.toString());
                }
                
                logger.debug("Database constraints validated successfully");
            } catch (ValidationException vex) {
                logger.error("Constraint validation exception: {}", vex.getMessage());
                loggingService.logSystemError(vex, "Database constraint validation failed", correlationId);
                throw vex;
            } catch (Exception ex) {
                logger.error("Unexpected error during constraint validation: {}", ex.getMessage(), ex);
                loggingService.logSystemError(ex, "Unexpected error in constraint validation", correlationId);
                throw new RuntimeException("Error al validar restricciones de la base de datos: " + ex.getMessage(), ex);
            }

            // Check backend connectivity before making request
            try {
                diagnosticsService.validateBackendConnectivity();
            } catch (Exception ex) {
                logger.error("Backend connectivity check failed: {}", ex.getMessage());
                loggingService.logSystemError(ex, "Backend connectivity check", correlationId);
                throw new RuntimeException("Servicio backend no disponible. Por favor intente más tarde.");
            }

            // Create order with transactional context and WebClient request/response logging
            try {
                String endpoint = apiBasePath + ORDERS_ENDPOINT;
                
                // Log WebClient request
                loggingService.logWebClientRequest(endpoint, HttpMethod.POST, correlationId);
                
                long requestStartTime = System.currentTimeMillis();
                OrderDTO createdOrder = webClient.post()
                        .uri(endpoint)
                        .bodyValue(orderDTO)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                long responseTime = System.currentTimeMillis() - requestStartTime;
                
                // Log WebClient response with performance metrics
                loggingService.logWebClientResponse(endpoint, 200, Duration.ofMillis(responseTime), correlationId);
                
                // Record compensation for distributed transaction support
                if (createdOrder != null && createdOrder.getOrdenCompraId() != null) {
                    transactionManagementService.recordCompensationForOrderCreation(
                        createdOrder.getOrdenCompraId(), correlationId
                    );
                }
                
                // Log successful order creation
                long totalDuration = System.currentTimeMillis() - operationStartTime;
                loggingService.logOrderCreationSuccess(createdOrder, Duration.ofMillis(totalDuration), correlationId);
                
                logger.info("Order created successfully - ID: {}, Reference: {}, Provider: {}, CorrelationId: {}", 
                    createdOrder.getOrdenCompraId(), createdOrder.getCodigoReferencia(), 
                    createdOrder.getProveedorId(), correlationId);
                
                return createdOrder;
                
            } catch (org.springframework.dao.DataAccessException daex) {
                logger.error("Database constraint violation during order creation: {}", daex.getMessage());
                
                // Handle database constraint violations with clear error messages (Requirement 8.4, 8.5)
                long responseTime = System.currentTimeMillis() - operationStartTime;
                loggingService.logWebClientResponse(
                    apiBasePath + ORDERS_ENDPOINT, 
                    500, 
                    Duration.ofMillis(responseTime), 
                    correlationId
                );
                
                logger.error("Database access exception during order creation");
                
                loggingService.logSystemError(daex, "Database error during order creation", correlationId);
                errorHandlingService.logDetailedError(daex, "Database error during order creation");
                
                throw new ValidationException("Error al guardar la orden. Por favor intente nuevamente.");
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error creating order: {}", ex.getMessage());
                
                // Log WebClient response with error status
                long responseTime = System.currentTimeMillis() - operationStartTime;
                loggingService.logWebClientResponse(
                    apiBasePath + ORDERS_ENDPOINT, 
                    ex.getStatusCode().value(), 
                    Duration.ofMillis(responseTime), 
                    correlationId
                );
                
                // Log system error with context
                Map<String, Object> errorContext = new HashMap<>();
                errorContext.put("provider", orderDTO.getProveedorId());
                errorContext.put("statusCode", ex.getStatusCode().value());
                errorContext.put("responseBody", ex.getResponseBodyAsString());
                loggingService.logOrderContext(orderDTO, errorContext, correlationId);
                loggingService.logSystemError(ex, "Order creation - WebClient error", correlationId);
                
                // Categorize error and provide appropriate response
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                
                // Use diagnostics service to analyze the failure
                DiagnosticReport report = diagnosticsService.diagnoseOrderCreationFailure(orderDTO, ex);
                logger.error("Diagnostic report for order creation failure: {}", report);
                
                errorHandlingService.logDetailedError(ex, 
                    "Order creation failed - Provider: " + orderDTO.getProveedorId());
                
                throw mapWebClientException(ex, "Error al crear la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error creating order: {}", vex.getMessage());
            loggingService.logSystemError(vex, "Order creation validation failed", correlationId);
            errorHandlingService.logDetailedError(vex, "Order creation validation failed");
            throw vex;
        } catch (Exception ex) {
            logger.error("Unexpected error creating order", ex);
            
            // Log unexpected error with full context
            long totalDuration = System.currentTimeMillis() - operationStartTime;
            loggingService.logSystemError(ex, "Unexpected error during order creation", correlationId);
            
            if (orderDTO != null) {
                Map<String, Object> errorContext = new HashMap<>();
                errorContext.put("operation", "createOrder");
                errorContext.put("exceptionType", ex.getClass().getSimpleName());
                loggingService.logOrderContext(orderDTO, errorContext, correlationId);
                
                DiagnosticReport report = diagnosticsService.diagnoseOrderCreationFailure(orderDTO, ex);
                logger.error("Diagnostic report for unexpected error: {}", report);
            }
            
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Order creation");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error during order creation");
            
            throw new RuntimeException("Error interno al crear la orden: " + ex.getMessage(), ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, timeout = 60)
    public OrderDTO updateOrder(Long ordenCompraId, OrderDTO orderDTO) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Updating order: {} with correlationId: {}", ordenCompraId, correlationId);
        
        try {
            if (ordenCompraId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }
            
            if (orderDTO == null) {
                throw new ValidationException("Los datos de la orden son obligatorios");
            }

            // Get current order to check state
            OrderDTO existingOrder = getOrderById(ordenCompraId);
            if (!existingOrder.isEditable()) {
                throw new BusinessRuleException(
                    String.format("No se puede modificar una orden en estado %s. Solo se pueden editar órdenes en estado BORRADOR.", 
                        existingOrder.getEstado())
                );
            }
            
            // Record compensation for distributed transaction support
            transactionManagementService.recordCompensationForOrderUpdate(ordenCompraId, existingOrder, correlationId);
            
            // Validate optimistic locking version if provided
            if (orderDTO.getVersion() != null) {
                try {
                    transactionManagementService.validateOptimisticLockVersion(
                        orderDTO.getVersion(), 
                        existingOrder.getVersion()
                    );
                    logger.debug("Optimistic locking validation passed for order {}", ordenCompraId);
                } catch (IllegalStateException ex) {
                    logger.warn("Optimistic locking conflict detected for order {}: {}", ordenCompraId, ex.getMessage());
                    throw new ConflictException(ex.getMessage());
                }
            }

            // Validate order data using validation service
            ValidationResult validationResult = validationService.validateOrderData(orderDTO);
            if (!validationResult.isValid()) {
                logger.warn("Order validation failed for update of order {}", ordenCompraId);
                StringBuilder errorMessage = new StringBuilder("Validación fallida: ");
                validationResult.getErrors().forEach(error -> 
                    errorMessage.append(error.getMessage()).append("; ")
                );
                throw new ValidationException(errorMessage.toString());
            }
            
            // Calculate total from details
            orderDTO = calculateOrderTotal(orderDTO);
            
            // Ensure ID matches
            orderDTO.setOrdenCompraId(ordenCompraId);

            try {
                OrderDTO updatedOrder = webClient.put()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}", ordenCompraId)
                        .bodyValue(orderDTO)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                // Increment version for optimistic locking
                Long newVersion = transactionManagementService.incrementOrderVersion(ordenCompraId);
                if (newVersion != null) {
                    updatedOrder.setVersion(newVersion);
                }
                
                logger.info("Order updated successfully - ID: {}, CorrelationId: {}, NewVersion: {}", 
                    ordenCompraId, correlationId, newVersion);
                return updatedOrder;
                        
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error updating order {}: {}", ordenCompraId, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Orden", ordenCompraId.toString());
                }
                // Execute compensation on failure
                transactionManagementService.executeCompensations(ordenCompraId, correlationId);
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, "Updating order: " + ordenCompraId);
                throw mapWebClientException(ex, "Error al actualizar la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error updating order {}: {}", ordenCompraId, vex.getMessage());
            throw vex;
        } catch (ConflictException cex) {
            logger.warn("Conflict error updating order {}: {}", ordenCompraId, cex.getMessage());
            throw cex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation updating order {}: {}", ordenCompraId, brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found for update: {}", ordenCompraId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error updating order {}", ordenCompraId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Order update");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error updating order: " + ordenCompraId);
            throw new RuntimeException("Error interno al actualizar la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, timeout = 60)
    public void deleteOrder(Long ordenCompraId) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Deleting order: {} with correlationId: {}", ordenCompraId, correlationId);
        
        try {
            if (ordenCompraId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }

            // Get current order to check state
            OrderDTO existingOrder = getOrderById(ordenCompraId);
            if (!existingOrder.isEditable()) {
                throw new BusinessRuleException(
                    String.format("No se puede eliminar una orden en estado %s. Solo se pueden eliminar órdenes en estado BORRADOR.", 
                        existingOrder.getEstado())
                );
            }

            try {
                webClient.delete()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}", ordenCompraId)
                        .retrieve()
                        .bodyToMono(Void.class)
                        .retryWhen(webClientRetry)
                        .block();
                        
                logger.info("Successfully deleted order: {} with correlationId: {}", ordenCompraId, correlationId);
                
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error deleting order {}: {}", ordenCompraId, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Orden", ordenCompraId.toString());
                }
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, "Deleting order: " + ordenCompraId);
                throw mapWebClientException(ex, "Error al eliminar la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error deleting order {}: {}", ordenCompraId, vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation deleting order {}: {}", ordenCompraId, brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found for deletion: {}", ordenCompraId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error deleting order {}", ordenCompraId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Order deletion");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error deleting order: " + ordenCompraId);
            throw new RuntimeException("Error interno al eliminar la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional(rollbackFor = { Exception.class }, timeout = 60)
    public OrderDTO transitionOrder(Long ordenCompraId, String newState) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Transitioning order {} to state: {} with correlationId: {}", ordenCompraId, newState, correlationId);
        
        try {
            if (ordenCompraId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }
            
            if (newState == null || newState.trim().isEmpty()) {
                throw new ValidationException("El nuevo estado es obligatorio");
            }
            
            newState = newState.trim().toUpperCase();

            // Get current order to validate transition
            OrderDTO existingOrder = getOrderById(ordenCompraId);
            
            if (!canTransitionTo(existingOrder.getEstado(), newState)) {
                throw new BusinessRuleException(
                    String.format("Transición inválida: no se puede cambiar de %s a %s. " +
                        "Transiciones válidas desde %s: %s", 
                        existingOrder.getEstado(), newState, 
                        existingOrder.getEstado(), 
                        VALID_TRANSITIONS.getOrDefault(existingOrder.getEstado(), List.of()))
                );
            }

            try {
                Map<String, String> stateTransition = Map.of("estado", newState);
                
                OrderDTO transitionedOrder = webClient.put()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}/estado", ordenCompraId)
                        .bodyValue(stateTransition)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                logger.info("Order transitioned successfully - ID: {}, From: {} To: {}, CorrelationId: {}", 
                    ordenCompraId, existingOrder.getEstado(), newState, correlationId);
                return transitionedOrder;
                        
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error transitioning order {} to state {}: {}", ordenCompraId, newState, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Orden", ordenCompraId.toString());
                } else if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    throw new BusinessRuleException("No se puede realizar esta transición de estado. Verifique las reglas de negocio.");
                }
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, 
                    "Transitioning order: " + ordenCompraId + " to state: " + newState);
                throw mapWebClientException(ex, "Error al cambiar el estado de la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error transitioning order {}: {}", ordenCompraId, vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation transitioning order {}: {}", ordenCompraId, brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found for transition: {}", ordenCompraId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error transitioning order {}", ordenCompraId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Order state transition");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, 
                "Unexpected error transitioning order: " + ordenCompraId);
            throw new RuntimeException("Error interno al cambiar el estado de la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    public OrderDTO calculateOrderTotal(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return orderDTO;
        }
        
        logger.debug("Calculating total for order with {} details", 
            orderDTO.getDetalles() != null ? orderDTO.getDetalles().size() : 0);
        
        // Use the DTO's built-in calculation method
        BigDecimal calculatedTotal = orderDTO.calculateTotal();
        
        logger.debug("Calculated total: {}", calculatedTotal);
        return orderDTO;
    }

    @Override
    public boolean canTransitionTo(String currentState, String newState) {
        if (currentState == null || newState == null) {
            return false;
        }
        
        List<String> validTransitions = VALID_TRANSITIONS.get(currentState);
        return validTransitions != null && validTransitions.contains(newState);
    }

    @Override
    public Page<OrderDTO> getOrdersByProvider(Long proveedorId, int page, int size) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Getting orders for provider {}, page={}, size={} with correlationId: {}", proveedorId, page, size, correlationId);
        
        try {
            if (proveedorId == null) {
                throw new ValidationException("El ID del proveedor es obligatorio");
            }

            Map<String, Object> params = Map.of(
                "proveedorId", proveedorId,
                "page", page,
                "size", size
            );

            String uri = buildUriWithParams(apiBasePath + ORDERS_ENDPOINT + "/by-provider", params);
            
            Page<OrderDTO> result = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(OrderPageResponse.class)
                    .retryWhen(webClientRetry)
                    .map(this::convertToPage)
                    .block();
            
            logger.info("Retrieved orders for provider {} - CorrelationId: {}", proveedorId, correlationId);
            return result;
                    
        } catch (ValidationException vex) {
            logger.error("Validation error getting orders for provider {}: {}", proveedorId, vex.getMessage());
            throw vex;
        } catch (WebClientResponseException ex) {
            logger.error("WebClient error retrieving orders for provider {}: {}", proveedorId, ex.getMessage());
            UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Getting orders for provider: " + proveedorId);
            throw mapWebClientException(ex, "Error al obtener las órdenes del proveedor");
        } catch (Exception ex) {
            logger.error("Unexpected error getting orders for provider {}", proveedorId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Getting orders by provider");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error getting orders for provider: " + proveedorId);
            throw new RuntimeException("Error interno al obtener órdenes del proveedor", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    public OrderDTO getOrderByReferenceCode(String codigoReferencia) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Getting order by reference code: {} with correlationId: {}", codigoReferencia, correlationId);
        
        try {
            if (codigoReferencia == null || codigoReferencia.trim().isEmpty()) {
                throw new ValidationException("El código de referencia es obligatorio");
            }

            OrderDTO order = webClient.get()
                    .uri(apiBasePath + ORDERS_ENDPOINT + "/by-reference/{codigo}", codigoReferencia.trim())
                    .retrieve()
                    .bodyToMono(OrderDTO.class)
                    .retryWhen(webClientRetry)
                    .block();
            
            logger.info("Retrieved order by reference code: {} - CorrelationId: {}", codigoReferencia, correlationId);
            return order;
                    
        } catch (ValidationException vex) {
            logger.error("Validation error getting order by reference {}: {}", codigoReferencia, vex.getMessage());
            throw vex;
        } catch (WebClientResponseException ex) {
            logger.error("WebClient error retrieving order by reference {}: {}", codigoReferencia, ex.getMessage());
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new EntityNotFoundException("Orden con código de referencia", codigoReferencia);
            }
            UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Getting order by reference code: " + codigoReferencia);
            throw mapWebClientException(ex, "Error al obtener la orden por código de referencia");
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found with reference code: {}", codigoReferencia);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error getting order by reference {}", codigoReferencia, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Getting order by reference code");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error getting order by reference: " + codigoReferencia);
            throw new RuntimeException("Error interno al obtener la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    public List<OrderDTO> getAllOrders(String estado, LocalDate fechaDesde, LocalDate fechaHasta) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Getting orders with filters - estado={}, fechaDesde={}, fechaHasta={} with correlationId: {}", 
            estado, fechaDesde, fechaHasta, correlationId);
        
        try {
            Map<String, Object> params = new HashMap<>();
            
            if (estado != null && !estado.trim().isEmpty()) {
                params.put("estado", estado.trim());
            }
            
            if (fechaDesde != null) {
                params.put("fechaDesde", fechaDesde.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            
            if (fechaHasta != null) {
                params.put("fechaHasta", fechaHasta.format(DateTimeFormatter.ISO_LOCAL_DATE));
            }

            String uri = buildUriWithParams(apiBasePath + ORDERS_ENDPOINT + "/filter", params);
            
            List<OrderDTO> orders = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToFlux(OrderDTO.class)
                    .retryWhen(webClientRetry)
                    .collectList()
                    .block();
            
            logger.info("Retrieved {} filtered orders - CorrelationId: {}", 
                orders != null ? orders.size() : 0, correlationId);
            return orders != null ? orders : List.of();
                    
        } catch (WebClientResponseException ex) {
            logger.error("WebClient error retrieving filtered orders: {}", ex.getMessage());
            UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Getting filtered orders");
            throw mapWebClientException(ex, "Error al obtener las órdenes filtradas");
        } catch (Exception ex) {
            logger.error("Unexpected error getting filtered orders", ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Getting filtered orders");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error getting filtered orders");
            throw new RuntimeException("Error interno al obtener órdenes filtradas", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional
    public OrderDTO transitionToEnviada(Long id) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Transitioning order {} to ENVIADA state with correlationId: {}", id, correlationId);
        
        try {
            if (id == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }

            // Get current order to validate state
            OrderDTO existingOrder = getOrderById(id);
            
            if (!"BORRADOR".equals(existingOrder.getEstado())) {
                throw new BusinessRuleException(
                    String.format("No se puede enviar una orden en estado %s. Solo se pueden enviar órdenes en estado BORRADOR.", 
                        existingOrder.getEstado())
                );
            }

            logger.info("Order transitioned to ENVIADA - ID: {}, CorrelationId: {}", id, correlationId);
            return transitionOrder(id, "ENVIADA");
            
        } catch (ValidationException vex) {
            logger.error("Validation error transitioning to ENVIADA: {}", vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation transitioning to ENVIADA: {}", brex.getMessage());
            throw brex;
        } catch (Exception ex) {
            logger.error("Unexpected error transitioning to ENVIADA", ex);
            throw ex;
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional
    public OrderDTO transitionToRecibida(Long id) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Transitioning order {} to RECIBIDA state with lot creation and correlationId: {}", id, correlationId);
        
        try {
            if (id == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }

            // Get current order to validate state
            OrderDTO existingOrder = getOrderById(id);
            
            if (!"ENVIADA".equals(existingOrder.getEstado())) {
                throw new BusinessRuleException(
                    String.format("No se puede recibir una orden en estado %s. Solo se pueden recibir órdenes en estado ENVIADA.", 
                        existingOrder.getEstado())
                );
            }

            try {
                // Use specific endpoint that handles lot creation
                OrderDTO receivedOrder = webClient.put()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}/receive", id)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                logger.info("Order transitioned to RECIBIDA with lot creation - ID: {}, CorrelationId: {}", id, correlationId);
                return receivedOrder;
                        
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error receiving order {} with lot creation: {}", id, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Orden", id.toString());
                } else if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    throw new BusinessRuleException("No se puede recibir la orden. Verifique que todos los productos tengan lotes disponibles.");
                }
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, "Receiving order: " + id);
                throw mapWebClientException(ex, "Error al recibir la orden y crear lotes");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error transitioning to RECIBIDA: {}", vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation transitioning to RECIBIDA: {}", brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found for receiving: {}", id);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error transitioning to RECIBIDA", ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Receiving order");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error receiving order: " + id);
            throw new RuntimeException("Error interno al recibir la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional
    public OrderDTO transitionToAnulada(Long id) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Transitioning order {} to ANULADA state with correlationId: {}", id, correlationId);
        
        try {
            if (id == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }

            // Get current order to validate state
            OrderDTO existingOrder = getOrderById(id);
            
            if ("RECIBIDA".equals(existingOrder.getEstado())) {
                throw new BusinessRuleException(
                    "No se puede anular una orden que ya ha sido recibida (estado RECIBIDA). " +
                    "Las órdenes recibidas no pueden ser modificadas."
                );
            }
            
            if ("ANULADA".equals(existingOrder.getEstado())) {
                throw new BusinessRuleException("La orden ya está anulada");
            }
            
            if (!"BORRADOR".equals(existingOrder.getEstado()) && !"ENVIADA".equals(existingOrder.getEstado())) {
                throw new BusinessRuleException(
                    String.format("No se puede anular una orden en estado %s. Solo se pueden anular órdenes en estado BORRADOR o ENVIADA.", 
                        existingOrder.getEstado())
                );
            }

            logger.info("Order transitioned to ANULADA - ID: {}, CorrelationId: {}", id, correlationId);
            return transitionOrder(id, "ANULADA");
            
        } catch (ValidationException vex) {
            logger.error("Validation error transitioning to ANULADA: {}", vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation transitioning to ANULADA: {}", brex.getMessage());
            throw brex;
        } catch (Exception ex) {
            logger.error("Unexpected error transitioning to ANULADA", ex);
            throw ex;
        } finally {
            clearMDC();
        }
    }

    @Override
    public BigDecimal calculateOrderTotal(List<OrderDetailDTO> details) {
        logger.debug("Calculating total for {} order details", details != null ? details.size() : 0);
        
        if (details == null || details.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = details.stream()
                .filter(detalle -> detalle.getCantidad() != null && detalle.getPrecioUnitario() != null)
                .map(detalle -> BigDecimal.valueOf(detalle.getCantidad())
                        .multiply(detalle.getPrecioUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.debug("Calculated total from details: {}", total);
        return total;
    }

    @Override
    @Transactional
    public OrderDTO addOrderDetail(Long orderId, OrderDetailDTO detail) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Adding detail to order {}: product={}, cantidad={} with correlationId: {}", 
            orderId, detail != null ? detail.getProductoId() : null, 
            detail != null ? detail.getCantidad() : null, correlationId);
        
        try {
            if (orderId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }
            
            if (detail == null) {
                throw new ValidationException("El detalle de la orden es obligatorio");
            }

            // Get current order to validate state
            OrderDTO existingOrder = getOrderById(orderId);
            
            if (!existingOrder.isEditable()) {
                throw new BusinessRuleException(
                    String.format("No se pueden agregar detalles a una orden en estado %s. Solo se pueden modificar órdenes en estado BORRADOR.", 
                        existingOrder.getEstado())
                );
            }

            // Validate detail data using validation service
            ValidationResult detailValidation = validationService.validateOrderDetails(List.of(detail));
            if (!detailValidation.isValid()) {
                logger.warn("Order detail validation failed for order {}", orderId);
                StringBuilder errorMessage = new StringBuilder("Validación de detalle fallida: ");
                detailValidation.getErrors().forEach(error -> 
                    errorMessage.append(error.getMessage()).append("; ")
                );
                throw new ValidationException(errorMessage.toString());
            }

            try {
                OrderDTO updatedOrder = webClient.post()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{id}/details", orderId)
                        .bodyValue(detail)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                logger.info("Detail added to order successfully - Order: {}, Product: {}, CorrelationId: {}", 
                    orderId, detail.getProductoId(), correlationId);
                return updatedOrder;
                        
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error adding detail to order {}: {}", orderId, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Orden", orderId.toString());
                } else if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    throw new BusinessRuleException("No se puede agregar el detalle. Verifique los datos del producto y cantidades.");
                }
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, "Adding detail to order: " + orderId);
                throw mapWebClientException(ex, "Error al agregar detalle a la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error adding detail to order {}: {}", orderId, vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation adding detail to order {}: {}", orderId, brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Order not found for adding detail: {}", orderId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error adding detail to order {}", orderId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Adding order detail");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, "Unexpected error adding detail to order: " + orderId);
            throw new RuntimeException("Error interno al agregar detalle a la orden", ex);
        } finally {
            clearMDC();
        }
    }

    @Override
    @Transactional
    public OrderDTO removeOrderDetail(Long orderId, Long detailId) {
        String correlationId = generateAndSetCorrelationId();
        logger.debug("Removing detail {} from order {} with correlationId: {}", detailId, orderId, correlationId);
        
        try {
            if (orderId == null) {
                throw new ValidationException("El ID de la orden es obligatorio");
            }
            
            if (detailId == null) {
                throw new ValidationException("El ID del detalle es obligatorio");
            }

            // Get current order to validate state
            OrderDTO existingOrder = getOrderById(orderId);
            
            if (!existingOrder.isEditable()) {
                throw new BusinessRuleException(
                    String.format("No se pueden eliminar detalles de una orden en estado %s. Solo se pueden modificar órdenes en estado BORRADOR.", 
                        existingOrder.getEstado())
                );
            }

            try {
                OrderDTO updatedOrder = webClient.delete()
                        .uri(apiBasePath + ORDERS_ENDPOINT + "/{orderId}/details/{detailId}", orderId, detailId)
                        .retrieve()
                        .bodyToMono(OrderDTO.class)
                        .retryWhen(webClientRetry)
                        .block();
                
                logger.info("Detail removed from order successfully - Order: {}, Detail: {}, CorrelationId: {}", 
                    orderId, detailId, correlationId);
                return updatedOrder;
                        
            } catch (WebClientResponseException ex) {
                logger.error("WebClient error removing detail {} from order {}: {}", detailId, orderId, ex.getMessage());
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new EntityNotFoundException("Detalle de orden", detailId.toString());
                } else if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    throw new BusinessRuleException("No se puede eliminar el detalle. La orden debe tener al menos un detalle.");
                }
                UserFriendlyError error = errorHandlingService.handleWebClientException(ex);
                error = resolveErrorWithContext(error, ex);
                errorHandlingService.logDetailedError(ex, 
                    "Removing detail: " + detailId + " from order: " + orderId);
                throw mapWebClientException(ex, "Error al eliminar detalle de la orden");
            }
            
        } catch (ValidationException vex) {
            logger.error("Validation error removing detail {} from order {}: {}", detailId, orderId, vex.getMessage());
            throw vex;
        } catch (BusinessRuleException brex) {
            logger.warn("Business rule violation removing detail {} from order {}: {}", detailId, orderId, brex.getMessage());
            throw brex;
        } catch (EntityNotFoundException enex) {
            logger.warn("Detail or order not found for removal - Order: {}, Detail: {}", orderId, detailId);
            throw enex;
        } catch (Exception ex) {
            logger.error("Unexpected error removing detail {} from order {}", detailId, orderId, ex);
            UserFriendlyError error = errorHandlingService.handleGenericException(ex, "Removing order detail");
            error = resolveErrorWithContext(error, ex);
            errorHandlingService.logDetailedError(ex, 
                "Unexpected error removing detail: " + detailId + " from order: " + orderId);
            throw new RuntimeException("Error interno al eliminar detalle de la orden", ex);
        } finally {
            clearMDC();
        }
    }

    /**
     * Validates a single order detail.
     * 
     * @param detail the order detail to validate
     * @throws ValidationException if validation fails
     */
    private void validateOrderDetail(OrderDetailDTO detail) {
        if (detail.getProductoId() == null) {
            throw new ValidationException("El producto es obligatorio");
        }
        if (detail.getCantidad() == null || detail.getCantidad() <= 0) {
            throw new ValidationException("La cantidad debe ser mayor a cero");
        }
        if (detail.getPrecioUnitario() == null || detail.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("El precio unitario debe ser mayor a cero");
        }
    }

    /**
     * Validates order data before creating or updating.
     * 
     * @param orderDTO the order to validate
     * @throws ValidationException if validation fails
     */
    private void validateOrderData(OrderDTO orderDTO) {
        if (orderDTO.getProveedorId() == null) {
            throw new ValidationException("El proveedor es obligatorio");
        }
        
        if (orderDTO.getDetalles() == null || orderDTO.getDetalles().isEmpty()) {
            throw new ValidationException("La orden debe tener al menos un detalle");
        }
        
        // Validate each detail
        for (int i = 0; i < orderDTO.getDetalles().size(); i++) {
            var detalle = orderDTO.getDetalles().get(i);
            if (detalle.getProductoId() == null) {
                throw new ValidationException(String.format("El producto en la línea %d es obligatorio", i + 1));
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new ValidationException(String.format("La cantidad en la línea %d debe ser mayor a cero", i + 1));
            }
            if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ValidationException(String.format("El precio unitario en la línea %d debe ser mayor a cero", i + 1));
            }
        }
    }

    /**
     * Maps WebClientResponseException to appropriate business exception.
     * 
     * @param ex the web client exception
     * @param contextMessage additional context message
     * @return the mapped business exception
     */
    private RuntimeException mapWebClientException(WebClientResponseException ex, String contextMessage) {
        return switch (ex.getStatusCode()) {
            case HttpStatus.BAD_REQUEST -> new ValidationException(contextMessage + ": datos inválidos");
            case HttpStatus.NOT_FOUND -> new EntityNotFoundException("Recurso", "solicitado");
            case HttpStatus.CONFLICT -> new ConflictException(contextMessage + ": conflicto con recursos existentes");
            case HttpStatus.UNPROCESSABLE_ENTITY -> new BusinessRuleException(contextMessage + ": violación de reglas de negocio");
            default -> new RuntimeException(contextMessage + ": error del servidor");
        };
    }

    /**
     * Builds URI with query parameters.
     * 
     * @param baseUri the base URI
     * @param params the parameters map
     * @return the URI with parameters
     */
    private String buildUriWithParams(String baseUri, Map<String, Object> params) {
        StringBuilder uri = new StringBuilder(baseUri);
        if (params != null && !params.isEmpty()) {
            uri.append("?");
            params.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .forEach(entry -> {
                    if (uri.charAt(uri.length() - 1) != '?') {
                        uri.append("&");
                    }
                    uri.append(entry.getKey()).append("=").append(entry.getValue());
                });
        }
        return uri.toString();
    }

    /**
     * Converts backend page response to Spring Data Page.
     * 
     * @param response the backend page response
     * @return Spring Data Page
     */
    private Page<OrderDTO> convertToPage(OrderPageResponse response) {
        return new PageImpl<>(
            response.getContent(),
            org.springframework.data.domain.PageRequest.of(response.getNumber(), response.getSize()),
            response.getTotalElements()
        );
    }

    /**
     * Response wrapper for paginated order data from backend.
     */
    private static class OrderPageResponse {
        private List<OrderDTO> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        // Getters and setters
        public List<OrderDTO> getContent() { return content; }
        public void setContent(List<OrderDTO> content) { this.content = content; }
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
    
    /**
     * Generates a unique reference code for orders.
     * Format: ORD-YYYYMMDD-HHMMSS-XXX where XXX is a random number
     * 
     * @return unique reference code
     */
    private String generateReferenceCode() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = now.format(formatter);
        int randomSuffix = (int) (Math.random() * 1000);
        return String.format("ORD-%s-%03d", timestamp, randomSuffix);
    }
}