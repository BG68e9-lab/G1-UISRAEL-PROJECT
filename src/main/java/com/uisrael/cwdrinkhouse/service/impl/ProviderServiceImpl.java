package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.configuration.AppConfigurationProperties;
import com.uisrael.cwdrinkhouse.dto.ProviderDTO;
import com.uisrael.cwdrinkhouse.exception.*;
import com.uisrael.cwdrinkhouse.service.ProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Implementation of ProviderService for provider management operations.
 * Uses WebClient for REST API communication with comprehensive validation and caching.
 * 
 * Requirements: 4.1-4.9, 14.1-14.9, 18.1-18.8
 */
@Service
public class ProviderServiceImpl implements ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderServiceImpl.class);

    // RUC validation pattern for Ecuador (13 digits)
    private static final Pattern RUC_PATTERN = Pattern.compile("\\d{13}");
    
    // Email validation pattern (basic)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private final WebClient webClient;
    private final AppConfigurationProperties appConfig;
    private final Retry retryConfiguration;
    
    // Simple in-memory cache for providers
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();

    @Autowired
    public ProviderServiceImpl(WebClient webClient, AppConfigurationProperties appConfig, Retry retryConfiguration) {
        this.webClient = webClient;
        this.appConfig = appConfig;
        this.retryConfiguration = retryConfiguration;
    }

    @Override
    public Page<ProviderDTO> getAllProviders(int page, int size) {
        logger.debug("Fetching providers with pagination: page={}, size={}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);

            // Try to get as Page first, fallback to List if backend returns array
            try {
                Map<String, Object> response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/proveedores")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                    .block();

                if (response != null && response.containsKey("content")) {
                    List<ProviderDTO> providers = convertToProviderList(response.get("content"));
                    long totalElements = ((Number) response.get("totalElements")).longValue();
                    return new PageImpl<>(providers, pageable, totalElements);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page for providers, converting: {}", pageException.getMessage());
            }

            // Fallback: try as a plain List
            List<ProviderDTO> providersList = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/proveedores")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProviderDTO>>() {})
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

            if (providersList != null) {
                int startIndex = page * size;
                int endIndex = Math.min(startIndex + size, providersList.size());
                List<ProviderDTO> pageContent = (startIndex < providersList.size())
                    ? providersList.subList(startIndex, endIndex)
                    : List.of();
                logger.info("Converted array to Page: {} providers (page {}, size {}, total {})",
                    pageContent.size(), page, size, providersList.size());
                return new PageImpl<>(pageContent, pageable, providersList.size());
            }

            return new PageImpl<>(List.of(), pageable, 0);

        } catch (WebClientResponseException e) {
            logger.error("Error fetching providers: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al obtener la lista de proveedores");
        } catch (Exception e) {
            logger.error("Unexpected error fetching providers", e);
            throw new ExternalServiceException("Error de conexión al obtener proveedores", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ProviderDTO> convertToProviderList(Object content) {
        if (content instanceof List) {
            return (List<ProviderDTO>) content;
        }
        return List.of();
    }

    @Override
    public List<ProviderDTO> getAllProviders() {
        logger.debug("Fetching all active providers");
        
        String cacheKey = "all_active_providers";
        
        // Check cache first
        if (isCacheValid(cacheKey)) {
            logger.debug("Returning cached providers list");
            return (List<ProviderDTO>) cache.get(cacheKey);
        }
        
        try {
            // The backend exposes GET /api/v1/proveedores (paginated or plain list).
            // Request a large page to retrieve all providers in one call.
            List<ProviderDTO> providers = null;

            // Try plain-list response first
            try {
                providers = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/proveedores")
                        .queryParam("page", 0)
                        .queryParam("size", 1000)
                        .build())
                    .retrieve()
                    .bodyToFlux(ProviderDTO.class)
                    .collectList()
                    .retryWhen(retryConfiguration)
                    .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                    .block();
            } catch (Exception listException) {
                logger.debug("Plain-list deserialization failed for providers, trying Page wrapper: {}", listException.getMessage());

                // Fallback: Page-wrapped response
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> page = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/proveedores")
                        .queryParam("page", 0)
                        .queryParam("size", 1000)
                        .build())
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<java.util.Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                    .block();

                if (page != null && page.containsKey("content")) {
                    providers = convertToProviderList(page.get("content"));
                }
            }

            if (providers == null) {
                providers = java.util.List.of();
            }

            // Cache the result
            cacheProviders(cacheKey, providers);
            
            return providers;

        } catch (WebClientResponseException e) {
            logger.error("Error fetching all providers: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al obtener la lista de proveedores");
        } catch (Exception e) {
            logger.error("Unexpected error fetching all providers", e);
            throw new ExternalServiceException("Error de conexión al obtener proveedores", e);
        }
    }

    @Override
    public ProviderDTO getProviderById(Long id) {
        logger.debug("Fetching provider by ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("El ID del proveedor es requerido");
        }
        
        try {
            return webClient
                .get()
                .uri("/proveedores/{id}", id)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Provider not found with ID: {}", id);
                throw new EntityNotFoundException("Proveedor no encontrado con ID: " + id);
            }
            logger.error("Error fetching provider by ID {}: HTTP {} - {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al obtener el proveedor");
        } catch (Exception e) {
            logger.error("Unexpected error fetching provider by ID: {}", id, e);
            throw new ExternalServiceException("Error de conexión al obtener el proveedor", e);
        }
    }

    @Override
    public ProviderDTO createProvider(ProviderDTO providerDTO) {
        logger.debug("Creating new provider with RUC: {}", providerDTO.getRuc());
        
        // Validate input
        validateProviderDTO(providerDTO);
        
        // Validate RUC format
        if (!validateRucFormat(providerDTO.getRuc())) {
            throw new ValidationException("El RUC debe tener exactamente 13 dígitos");
        }
        
        // Validate email format
        if (!validateEmailFormat(providerDTO.getEmail())) {
            throw new ValidationException("El formato del email es inválido");
        }
        
        try {
            ProviderDTO createdProvider = webClient
                .post()
                .uri("/proveedores")
                .bodyValue(providerDTO)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendWriteTimeout()))
                .block();

            // Invalidate cache after creation
            invalidateProvidersCache();
            
            logger.info("Successfully created provider with ID: {}", createdProvider.getProveedorId());
            return createdProvider;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn("Conflict creating provider with RUC: {}", providerDTO.getRuc());
                throw new ConflictException("Ya existe un proveedor con el RUC: " + providerDTO.getRuc());
            }
            logger.error("Error creating provider: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al crear el proveedor");
        } catch (Exception e) {
            logger.error("Unexpected error creating provider", e);
            throw new ExternalServiceException("Error de conexión al crear el proveedor", e);
        }
    }

    @Override
    public ProviderDTO updateProvider(Long id, ProviderDTO providerDTO) {
        logger.debug("Updating provider with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("El ID del proveedor es requerido");
        }
        
        // Validate input
        validateProviderDTO(providerDTO);
        
        // Validate RUC format
        if (!validateRucFormat(providerDTO.getRuc())) {
            throw new ValidationException("El RUC debe tener exactamente 13 dígitos");
        }
        
        // Validate email format
        if (!validateEmailFormat(providerDTO.getEmail())) {
            throw new ValidationException("El formato del email es inválido");
        }
        
        try {
            ProviderDTO updatedProvider = webClient
                .put()
                .uri("/proveedores/{id}", id)
                .bodyValue(providerDTO)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendWriteTimeout()))
                .block();

            // Invalidate cache after update
            invalidateProvidersCache();
            
            logger.info("Successfully updated provider with ID: {}", id);
            return updatedProvider;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Provider not found for update with ID: {}", id);
                throw new EntityNotFoundException("Proveedor no encontrado con ID: " + id);
            }
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn("Conflict updating provider with RUC: {}", providerDTO.getRuc());
                throw new ConflictException("Ya existe otro proveedor con el RUC: " + providerDTO.getRuc());
            }
            logger.error("Error updating provider ID {}: HTTP {} - {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al actualizar el proveedor");
        } catch (Exception e) {
            logger.error("Unexpected error updating provider ID: {}", id, e);
            throw new ExternalServiceException("Error de conexión al actualizar el proveedor", e);
        }
    }

    @Override
    public void deleteProvider(Long id) {
        logger.debug("Deleting provider with ID: {}", id);
        
        if (id == null) {
            throw new ValidationException("El ID del proveedor es requerido");
        }
        
        try {
            webClient
                .delete()
                .uri("/proveedores/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendWriteTimeout()))
                .block();

            // Invalidate cache after deletion
            invalidateProvidersCache();
            
            logger.info("Successfully deleted provider with ID: {}", id);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Provider not found for deletion with ID: {}", id);
                throw new EntityNotFoundException("Proveedor no encontrado con ID: " + id);
            }
            if (e.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                logger.warn("Cannot delete provider with ID {} due to dependencies", id);
                throw new BusinessRuleException("No se puede eliminar el proveedor porque tiene órdenes de compra asociadas");
            }
            logger.error("Error deleting provider ID {}: HTTP {} - {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al eliminar el proveedor");
        } catch (Exception e) {
            logger.error("Unexpected error deleting provider ID: {}", id, e);
            throw new ExternalServiceException("Error de conexión al eliminar el proveedor", e);
        }
    }

    @Override
    public List<ProviderDTO> searchProviders(String razonSocial, String ruc) {
        logger.debug("Searching providers with razonSocial: '{}', ruc: '{}'", razonSocial, ruc);
        
        try {
            return webClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/proveedores/buscar");
                    if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                        builder.queryParam("razonSocial", razonSocial.trim());
                    }
                    if (ruc != null && !ruc.trim().isEmpty()) {
                        builder.queryParam("ruc", ruc.trim());
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToFlux(ProviderDTO.class)
                .collectList()
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

        } catch (WebClientResponseException e) {
            logger.error("Error searching providers: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al buscar proveedores");
        } catch (Exception e) {
            logger.error("Unexpected error searching providers", e);
            throw new ExternalServiceException("Error de conexión al buscar proveedores", e);
        }
    }

    @Override
    public Page<ProviderDTO> searchProviders(String razonSocial, String ruc, int page, int size) {
        logger.debug("Searching providers with pagination: razonSocial='{}', ruc='{}', page={}, size={}", 
                     razonSocial, ruc, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);

            // Try Page first, fallback to List
            try {
                Map<String, Object> response = webClient
                    .get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/proveedores/buscar")
                            .queryParam("page", page)
                            .queryParam("size", size);
                        if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                            builder.queryParam("razonSocial", razonSocial.trim());
                        }
                        if (ruc != null && !ruc.trim().isEmpty()) {
                            builder.queryParam("ruc", ruc.trim());
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .retryWhen(retryConfiguration)
                    .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                    .block();

                if (response != null && response.containsKey("content")) {
                    List<ProviderDTO> providers = convertToProviderList(response.get("content"));
                    long totalElements = ((Number) response.get("totalElements")).longValue();
                    return new PageImpl<>(providers, pageable, totalElements);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array for provider search, converting: {}", pageException.getMessage());
            }

            // Fallback to List
            List<ProviderDTO> providersList = webClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/proveedores/buscar")
                        .queryParam("page", page)
                        .queryParam("size", size);
                    if (razonSocial != null && !razonSocial.trim().isEmpty()) {
                        builder.queryParam("razonSocial", razonSocial.trim());
                    }
                    if (ruc != null && !ruc.trim().isEmpty()) {
                        builder.queryParam("ruc", ruc.trim());
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProviderDTO>>() {})
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

            if (providersList != null) {
                int startIndex = page * size;
                int endIndex = Math.min(startIndex + size, providersList.size());
                List<ProviderDTO> pageContent = (startIndex < providersList.size())
                    ? providersList.subList(startIndex, endIndex)
                    : List.of();
                return new PageImpl<>(pageContent, pageable, providersList.size());
            }

            return new PageImpl<>(List.of(), pageable, 0);

        } catch (WebClientResponseException e) {
            logger.error("Error searching providers with pagination: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al buscar proveedores");
        } catch (Exception e) {
            logger.error("Unexpected error searching providers with pagination", e);
            throw new ExternalServiceException("Error de conexión al buscar proveedores", e);
        }
    }

    @Override
    public boolean validateRucFormat(String ruc) {
        if (ruc == null || ruc.trim().isEmpty()) {
            return false;
        }
        
        String cleanRuc = ruc.trim();
        
        // Check basic format (13 digits)
        if (!RUC_PATTERN.matcher(cleanRuc).matches()) {
            return false;
        }
        
        // Additional Ecuador RUC validation could be added here
        // For now, we just check the 13-digit format
        
        logger.debug("RUC format validation passed for: {}", cleanRuc);
        return true;
    }

    @Override
    public boolean isRucUnique(String ruc, Long excludeId) {
        logger.debug("Checking RUC uniqueness for: {}, excludeId: {}", ruc, excludeId);
        
        if (ruc == null || ruc.trim().isEmpty()) {
            return false;
        }
        
        try {
            Map<String, Object> response = webClient
                .get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path("/proveedores/verificar-ruc")
                        .queryParam("ruc", ruc.trim());
                    if (excludeId != null) {
                        builder.queryParam("excludeId", excludeId);
                    }
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

            if (response != null && response.containsKey("unique")) {
                return (Boolean) response.get("unique");
            }
            return true; // assume unique if response is unexpected

        } catch (WebClientResponseException e) {
            logger.error("Error checking RUC uniqueness: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            // In case of error, assume not unique for safety
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error checking RUC uniqueness", e);
            return false;
        }
    }

    @Override
    public boolean validateEmailFormat(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String cleanEmail = email.trim();
        boolean isValid = EMAIL_PATTERN.matcher(cleanEmail).matches();
        
        logger.debug("Email format validation for '{}': {}", cleanEmail, isValid);
        return isValid;
    }

    @Override
    public List<ProviderDTO> getProvidersByActiveStatus(boolean activo) {
        logger.debug("Fetching providers by active status: {}", activo);
        
        try {
            return webClient
                .get()
                .uri("/proveedores/por-estado?activo=" + activo)
                .retrieve()
                .bodyToFlux(ProviderDTO.class)
                .collectList()
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendReadTimeout()))
                .block();

        } catch (WebClientResponseException e) {
            logger.error("Error fetching providers by status: HTTP {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al obtener proveedores por estado");
        } catch (Exception e) {
            logger.error("Unexpected error fetching providers by status", e);
            throw new ExternalServiceException("Error de conexión al obtener proveedores por estado", e);
        }
    }

    @Override
    public ProviderDTO updateProviderActiveStatus(Long id, boolean activo) {
        logger.debug("Updating provider active status: ID={}, activo={}", id, activo);
        
        if (id == null) {
            throw new ValidationException("El ID del proveedor es requerido");
        }
        
        try {
            Map<String, Object> statusUpdate = Map.of("activo", activo);
            
            ProviderDTO updatedProvider = webClient
                .patch()
                .uri("/proveedores/{id}/estado", id)
                .bodyValue(statusUpdate)
                .retrieve()
                .bodyToMono(ProviderDTO.class)
                .retryWhen(retryConfiguration)
                .timeout(Duration.ofSeconds(appConfig.getBackendWriteTimeout()))
                .block();

            // Invalidate cache after status update
            invalidateProvidersCache();
            
            logger.info("Successfully updated provider status: ID={}, activo={}", id, activo);
            return updatedProvider;

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.warn("Provider not found for status update with ID: {}", id);
                throw new EntityNotFoundException("Proveedor no encontrado con ID: " + id);
            }
            logger.error("Error updating provider status ID {}: HTTP {} - {}", id, e.getStatusCode(), e.getResponseBodyAsString());
            throw handleWebClientException(e, "Error al actualizar el estado del proveedor");
        } catch (Exception e) {
            logger.error("Unexpected error updating provider status ID: {}", id, e);
            throw new ExternalServiceException("Error de conexión al actualizar el estado del proveedor", e);
        }
    }

    /**
     * Validates the provider DTO for required fields and basic constraints.
     */
    private void validateProviderDTO(ProviderDTO providerDTO) {
        if (providerDTO == null) {
            throw new ValidationException("Los datos del proveedor son requeridos");
        }
        
        if (providerDTO.getRuc() == null || providerDTO.getRuc().trim().isEmpty()) {
            throw new ValidationException("El RUC es obligatorio");
        }
        
        if (providerDTO.getRazonSocial() == null || providerDTO.getRazonSocial().trim().isEmpty()) {
            throw new ValidationException("La razón social es obligatoria");
        }
        
        if (providerDTO.getDireccion() == null || providerDTO.getDireccion().trim().isEmpty()) {
            throw new ValidationException("La dirección es obligatoria");
        }
        
        if (providerDTO.getEmail() == null || providerDTO.getEmail().trim().isEmpty()) {
            throw new ValidationException("El email es obligatorio");
        }
    }

    /**
     * Handles WebClient exceptions and converts them to appropriate business exceptions.
     */
    private RuntimeException handleWebClientException(WebClientResponseException e, String defaultMessage) {
        switch (e.getStatusCode().value()) {
            case 400:
                return new ValidationException("Datos inválidos: " + e.getResponseBodyAsString());
            case 401:
                return new ExternalServiceException("No autorizado para realizar esta operación");
            case 403:
                return new ExternalServiceException("Acceso denegado");
            case 404:
                return new EntityNotFoundException("Recurso no encontrado");
            case 409:
                return new ConflictException("Conflicto: " + e.getResponseBodyAsString());
            case 422:
                return new BusinessRuleException("No se puede procesar la solicitud: " + e.getResponseBodyAsString());
            case 429:
                return new ExternalServiceException("Límite de cuota excedido, intente más tarde");
            case 500:
                return new ExternalServiceException("Error interno del servidor");
            case 503:
                return new ExternalServiceException("Servicio no disponible, intente más tarde");
            default:
                return new ExternalServiceException(defaultMessage + ": " + e.getResponseBodyAsString());
        }
    }

    /**
     * Checks if cached data is still valid based on TTL.
     */
    private boolean isCacheValid(String key) {
        if (!appConfig.isCacheEnabled()) {
            return false;
        }
        
        Long timestamp = cacheTimestamps.get(key);
        if (timestamp == null || !cache.containsKey(key)) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        long cacheAge = currentTime - timestamp;
        long maxAge = appConfig.getCacheTtlProviders() * 1000; // Convert to milliseconds
        
        return cacheAge < maxAge;
    }

    /**
     * Caches providers list with timestamp.
     */
    private void cacheProviders(String key, List<ProviderDTO> providers) {
        if (appConfig.isCacheEnabled()) {
            cache.put(key, providers);
            cacheTimestamps.put(key, System.currentTimeMillis());
            logger.debug("Cached providers list with key: {}", key);
        }
    }

    /**
     * Invalidates all provider-related cache entries.
     */
    private void invalidateProvidersCache() {
        if (appConfig.isCacheEnabled()) {
            cache.clear();
            cacheTimestamps.clear();
            logger.debug("Invalidated providers cache");
        }
    }
}
