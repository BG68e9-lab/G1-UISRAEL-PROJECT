package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.ProductDTO;
import com.uisrael.cwdrinkhouse.dto.IAIdentificationResultDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.CacheManager;
import com.uisrael.cwdrinkhouse.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of ProductService using WebClient for backend API communication.
 * Provides complete CRUD operations with search functionality, pagination, caching, and AI identification.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Advanced search with filters (nombre, marca, tipo)
 * - Pagination support for large datasets
 * - Cache invalidation on write operations (products are mutable)
 * - AI-based product identification with base64 image processing
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 3.2-3.12, 12.3-12.7, 18.5-18.6
 */
@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private static final String PRODUCTS_ENDPOINT = "/api/v1/productos";
    private static final String PRODUCT_BY_ID_ENDPOINT = "/api/v1/productos/{id}";
    private static final String PRODUCTS_SEARCH_ENDPOINT = "/api/v1/productos/buscar";
    private static final String PRODUCTS_BY_CATEGORY_ENDPOINT = "/api/v1/productos/categoria/{categoriaId}";
    private static final String PRODUCTS_IA_IDENTIFY_ENDPOINT = "/api/v1/productos/identificar-ia";
    
    private static final String PRODUCT_CACHE_KEY_PREFIX = "product:";

    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final Retry retryConfiguration;

    @Autowired
    public ProductServiceImpl(WebClient webClient, CacheManager cacheManager, Retry retryConfiguration) {
        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.retryConfiguration = retryConfiguration;
    }

    @Override
    public Page<ProductDTO> getProducts(int page, int size) {
        logger.debug("Retrieving products with pagination: page={}, size={}", page, size);
        
        validatePaginationParameters(page, size);

        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("page", page);
            queryParams.put("size", size);

            String uriTemplate = PRODUCTS_ENDPOINT + "?page={page}&size={size}";
            
            // Note: Products are mutable, so no caching for list operations
            // to ensure fresh data on each request
            
            // Try to get as paginated response first, fallback to List if backend returns array
            try {
                ProductPageResponse pageResponse = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(ProductPageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();

                if (pageResponse != null && pageResponse.getContent() != null) {
                    logger.info("Retrieved {} products (page {}, size {}, total {})", 
                               pageResponse.getContent().size(), page, size, pageResponse.getTotalElements());
                    return convertToPage(pageResponse);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page object, converting to Page: {}", pageException.getMessage());
                
                // Try to get as List and convert to Page
                List<ProductDTO> productsList = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ProductDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();

                if (productsList != null) {
                    // Calculate pagination for array response
                    int startIndex = page * size;
                    int endIndex = Math.min(startIndex + size, productsList.size());
                    
                    List<ProductDTO> pageContent = (startIndex < productsList.size()) 
                        ? productsList.subList(startIndex, endIndex) 
                        : List.of();
                    
                    Page<ProductDTO> convertedPage = new PageImpl<>(pageContent, PageRequest.of(page, size), productsList.size());
                    
                    logger.info("Converted array to Page: {} products (page {}, size {}, total {})", 
                               pageContent.size(), page, size, productsList.size());
                    return convertedPage;
                } else {
                    logger.warn("Backend returned null for products list");
                    return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
                }
            }
            
            logger.warn("Backend returned null for products list");
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving products (page %d, size %d): %s - %s", 
                    page, size, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Error retrieving products (page %d, size %d) from backend: %s", 
                    page, size, e.getMessage());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<ProductDTO> getProducts(int page, int size, String search) {
        logger.debug("Retrieving products with search: page={}, size={}, search='{}'", page, size, search);
        
        validatePaginationParameters(page, size);

        // If search is null or empty, delegate to basic getProducts
        if (!StringUtils.hasText(search)) {
            return getProducts(page, size);
        }

        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("page", page);
            queryParams.put("size", size);
            queryParams.put("search", search);

            String uriTemplate = PRODUCTS_ENDPOINT + "?page={page}&size={size}&search={search}";
            
            // Try paginated response first, fallback to List (backend may return plain array)
            try {
                ProductPageResponse pageResponse = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(ProductPageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();

                if (pageResponse != null && pageResponse.getContent() != null) {
                    logger.info("Retrieved {} products matching search '{}' (page {}, size {}, total {})", 
                               pageResponse.getContent().size(), search, page, size, pageResponse.getTotalElements());
                    return convertToPage(pageResponse);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page object for search, converting to Page: {}", pageException.getMessage());
                
                // Try to get as List and convert to Page
                List<ProductDTO> productsList = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ProductDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();

                if (productsList != null) {
                    // Calculate pagination for array response
                    int startIndex = page * size;
                    int endIndex = Math.min(startIndex + size, productsList.size());
                    
                    List<ProductDTO> pageContent = (startIndex < productsList.size()) 
                        ? productsList.subList(startIndex, endIndex) 
                        : List.of();
                    
                    Page<ProductDTO> convertedPage = new PageImpl<>(pageContent, PageRequest.of(page, size), productsList.size());
                    
                    logger.info("Converted search array to Page: {} products matching '{}' (page {}, size {}, total {})", 
                               pageContent.size(), search, page, size, productsList.size());
                    return convertedPage;
                } else {
                    logger.warn("Backend returned null for products search");
                    return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
                }
            }
            
            logger.warn("Backend returned null for products search");
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while searching products (search '%s', page %d, size %d): %s - %s", 
                    search, page, size, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Error searching products (search '%s', page %d, size %d): %s", 
                    search, page, size, e.getMessage());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<ProductDTO> searchProducts(String nombre, String marca, String tipo, int page, int size) {
        logger.debug("Searching products with filters: nombre='{}', marca='{}', tipo='{}', page={}, size={}", 
                    nombre, marca, tipo, page, size);
        
        validatePaginationParameters(page, size);

        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("page", page);
            queryParams.put("size", size);
            
            StringBuilder uriBuilder = new StringBuilder(PRODUCTS_SEARCH_ENDPOINT + "?page={page}&size={size}");
            
            if (StringUtils.hasText(nombre)) {
                uriBuilder.append("&nombre={nombre}");
                queryParams.put("nombre", nombre.trim());
            }
            if (StringUtils.hasText(marca)) {
                uriBuilder.append("&marca={marca}");
                queryParams.put("marca", marca.trim());
            }
            if (StringUtils.hasText(tipo)) {
                uriBuilder.append("&tipo={tipo}");
                queryParams.put("tipo", tipo.trim());
            }
            
            String uriTemplate = uriBuilder.toString();

            // Try Page response first, fallback to List (backend may return plain array)
            try {
                ProductPageResponse pageResponse = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(ProductPageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();

                if (pageResponse != null && pageResponse.getContent() != null) {
                    logger.info("Retrieved {} products with advanced search (page {}, size {}, total {})",
                               pageResponse.getContent().size(), page, size, pageResponse.getTotalElements());
                    return convertToPage(pageResponse);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page for advanced search, converting: {}",
                             pageException.getMessage());

                List<ProductDTO> productsList = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ProductDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();

                if (productsList != null) {
                    int startIndex = page * size;
                    int endIndex = Math.min(startIndex + size, productsList.size());
                    List<ProductDTO> pageContent = (startIndex < productsList.size())
                            ? productsList.subList(startIndex, endIndex)
                            : List.of();
                    Page<ProductDTO> convertedPage = new PageImpl<>(pageContent, PageRequest.of(page, size), productsList.size());
                    logger.info("Converted advanced-search array to Page: {} products (total {})",
                               pageContent.size(), productsList.size());
                    return convertedPage;
                }
            }

            logger.warn("Backend returned null for products advanced search");
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);

        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error during products advanced search (page %d, size %d): %s - %s", 
                    page, size, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("Error during products advanced search (page %d, size %d): %s", 
                    page, size, e.getMessage());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public ProductDTO getProductById(Long id) {
        logger.debug("Retrieving product with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        try {
            // Primero capturamos el JSON raw para ver exactamente qué campos devuelve el backend
            @SuppressWarnings("unchecked")
            Map<String, Object> rawResponse = webClient.get()
                    .uri(PRODUCT_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (rawResponse != null) {
                logger.info("Backend raw response keys for product {}: {}", id, rawResponse.keySet());
                logger.info("Backend raw response for product {}: {}", id, rawResponse);
                ProductDTO product = mapRawToProductDTO(rawResponse);
                return product;
            } else {
                throw new EntityNotFoundException("Producto", id.toString());
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Product {} not found in backend", id);
                throw new EntityNotFoundException("Producto", id.toString());
            }
            
            String errorMsg = String.format("Backend API error while retrieving product %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "Error retrieving product " + id + " from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Mapea el JSON raw del backend al ProductDTO manejando diferentes nombres de campos.
     */
    @SuppressWarnings("unchecked")
    private ProductDTO mapRawToProductDTO(Map<String, Object> raw) {
        ProductDTO dto = new ProductDTO();

        // ID — puede venir como productoId o id
        Object pid = raw.get("productoId");
        if (pid == null) pid = raw.get("id");
        if (pid != null) dto.setProductoId(((Number) pid).longValue());

        dto.setNombre((String) raw.getOrDefault("nombre", raw.get("name")));
        dto.setMarca((String) raw.getOrDefault("marca", raw.get("brand")));
        dto.setTipo((String) raw.getOrDefault("tipo", raw.get("type")));
        dto.setCodigoBarras((String) raw.getOrDefault("codigoBarras", raw.get("barcode")));
        dto.setUsuarioCreacion((String) raw.get("usuarioCreacion"));

        // Categoría — puede venir como categoriaId, categoria_id, o como objeto anidado
        Object catId = raw.get("categoriaId");
        if (catId == null) catId = raw.get("categoria_id");
        if (catId == null) {
            // Puede venir como objeto anidado { categoria: { categoriaId: X } }
            Object catObj = raw.get("categoria");
            if (catObj instanceof Map) {
                catId = ((Map<String, Object>) catObj).get("categoriaId");
                if (catId == null) catId = ((Map<String, Object>) catObj).get("id");
                if (catId == null) catId = ((Map<String, Object>) catObj).get("categoria_id");
            }
            // También puede venir como { fkCategoria: { categoriaId: X } }
            if (catId == null) {
                Object fkCat = raw.get("fkCategoria");
                if (fkCat == null) fkCat = raw.get("fkCategoriaEntity");
                if (fkCat instanceof Map) {
                    catId = ((Map<String, Object>) fkCat).get("categoriaId");
                    if (catId == null) catId = ((Map<String, Object>) fkCat).get("id");
                }
            }
        }
        if (catId != null) dto.setCategoriaId(((Number) catId).longValue());
        logger.info("Mapped categoriaId={} from raw keys: {}", dto.getCategoriaId(), raw.keySet());

        // Precios
        Object costo = raw.get("costoPromedio");
        if (costo != null) dto.setCostoPromedio(new java.math.BigDecimal(costo.toString()));

        Object margen = raw.get("margenGanancia");
        if (margen != null) dto.setMargenGanancia(new java.math.BigDecimal(margen.toString()));

        Object precioVenta = raw.get("precioVenta");
        if (precioVenta != null) dto.setPrecioVenta(new java.math.BigDecimal(precioVenta.toString()));

        Object precioPersonalizado = raw.get("precioPersonalizado");
        if (precioPersonalizado instanceof Boolean) dto.setPrecioPersonalizado((Boolean) precioPersonalizado);

        // Fecha
        Object fechaCreacion = raw.get("fechaCreacion");
        if (fechaCreacion == null) fechaCreacion = raw.get("creadoEn");
        if (fechaCreacion != null) {
            try {
                String fechaStr = fechaCreacion.toString();
                if (fechaStr.length() >= 19) {
                    dto.setFechaCreacion(java.time.LocalDateTime.parse(fechaStr.substring(0, 19)));
                }
            } catch (Exception ex) {
                logger.warn("Could not parse fechaCreacion: {}", fechaCreacion);
            }
        }

        return dto;
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        logger.debug("Creating new product: {}", productDTO.getNombre());
        
        if (productDTO == null) {
            throw new IllegalArgumentException("ProductDTO cannot be null");
        }
        
        // Validate conditional precioVenta requirement
        validateConditionalPrecioVenta(productDTO);
        
        // Ensure ID is null for creation
        productDTO.setProductoId(null);

        try {
            ProductDTO createdProduct = webClient.post()
                    .uri(PRODUCTS_ENDPOINT)
                    .bodyValue(productDTO)
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdProduct != null) {
                // Invalidate any cached data since products are mutable
                invalidateProductsCache();
                
                logger.info("Successfully created product with ID: {}", createdProduct.getProductoId());
                return createdProduct;
            } else {
                throw new RuntimeException("Backend returned null after product creation");
            }
            
        } catch (WebClientResponseException e) {
            handleWriteOperationError(e, "create", productDTO.getNombre());
            return null; // This line will never be reached due to exception throwing above
        } catch (Exception e) {
            String errorMsg = "Error creating product: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        logger.debug("Updating product with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (productDTO == null) {
            throw new IllegalArgumentException("ProductDTO cannot be null");
        }
        
        // Validate conditional precioVenta requirement
        validateConditionalPrecioVenta(productDTO);
        
        // Ensure the ID matches
        productDTO.setProductoId(id);

        try {
            ProductDTO updatedProduct = webClient.put()
                    .uri(PRODUCT_BY_ID_ENDPOINT, id)
                    .bodyValue(productDTO)
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedProduct != null) {
                // Invalidate any cached data since products are mutable
                invalidateProductsCache();
                
                logger.info("Successfully updated product with ID: {}", id);
                return updatedProduct;
            } else {
                throw new RuntimeException("Backend returned null after product update");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Product {} not found for update", id);
                throw new EntityNotFoundException("Producto", id.toString());
            }
            
            handleWriteOperationError(e, "update", productDTO.getNombre());
            return null; // This line will never be reached due to exception throwing above
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error updating product " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        logger.debug("Deleting product with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        try {
            webClient.delete()
                    .uri(PRODUCT_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            // Invalidate any cached data since products are mutable
            invalidateProductsCache();
            
            logger.info("Successfully deleted product with ID: {}", id);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Product {} not found for deletion", id);
                throw new EntityNotFoundException("Producto", id.toString());
            }
            if (e.getStatusCode().value() == 422) {
                logger.debug("Product {} cannot be deleted due to business rules", id);
                throw new BusinessRuleException("No se puede eliminar el producto porque tiene dependencias asociadas (lotes, movimientos de inventario, etc.)");
            }
            
            String errorMsg = String.format("Backend API error while deleting product %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | BusinessRuleException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error deleting product " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public IAIdentificationResultDTO identifyProductByIA(String imagenBase64) {
        logger.debug("Identifying product using IA with image data length: {}", 
                    imagenBase64 != null ? imagenBase64.length() : 0);
        
        if (!StringUtils.hasText(imagenBase64)) {
            throw new IllegalArgumentException("Base64 image data cannot be null or empty");
        }
        
        // Basic validation of base64 format
        if (imagenBase64.length() < 100) {
            throw new ValidationException("La imagen debe ser válida (mínimo 100 caracteres en base64)");
        }

        try {
            // Create request payload for IA identification
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("imagenBase64", imagenBase64);

            IAIdentificationResultDTO result = webClient.post()
                    .uri(PRODUCTS_IA_IDENTIFY_ENDPOINT)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(IAIdentificationResultDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (result != null) {
                logger.info("IA identification completed with confidence: {} ({}%)", 
                           result.getNivelConfianza(), result.getPorcentajeConfianza());
                return result;
            } else {
                throw new RuntimeException("Backend returned null after IA identification");
            }
            
        } catch (WebClientResponseException e) {
            handleIAIdentificationError(e, imagenBase64);
            return null; // This line will never be reached due to exception throwing above
        } catch (Exception e) {
            String errorMsg = "Error during IA product identification: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size) {
        logger.debug("Retrieving products by category: categoryId={}, page={}, size={}", categoryId, page, size);
        
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        
        validatePaginationParameters(page, size);

        try {
            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("categoryId", categoryId);
            queryParams.put("page", page);
            queryParams.put("size", size);

            String uriTemplate = PRODUCTS_BY_CATEGORY_ENDPOINT + "?page={page}&size={size}";
            
            // Try paginated response first, fallback to List if backend returns array
            try {
                ProductPageResponse pageResponse = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(ProductPageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();

                if (pageResponse != null && pageResponse.getContent() != null) {
                    logger.info("Retrieved {} products for category {} (page {}, size {}, total {})", 
                               pageResponse.getContent().size(), categoryId, page, size, pageResponse.getTotalElements());
                    return convertToPage(pageResponse);
                }
            } catch (Exception pageException) {
                logger.debug("Backend returned array instead of Page object for category {}, converting to Page: {}", categoryId, pageException.getMessage());
                
                // Try to get as List and convert to Page
                List<ProductDTO> productsList = webClient.get()
                        .uri(uriTemplate, queryParams)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<ProductDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();

                if (productsList != null) {
                    // Calculate pagination for array response
                    int startIndex = page * size;
                    int endIndex = Math.min(startIndex + size, productsList.size());
                    
                    List<ProductDTO> pageContent = (startIndex < productsList.size()) 
                        ? productsList.subList(startIndex, endIndex) 
                        : List.of();
                    
                    Page<ProductDTO> convertedPage = new PageImpl<>(pageContent, PageRequest.of(page, size), productsList.size());
                    
                    logger.info("Converted category array to Page: {} products for category {} (page {}, size {}, total {})", 
                               pageContent.size(), categoryId, page, size, productsList.size());
                    return convertedPage;
                } else {
                    logger.warn("Backend returned null for products by category");
                    return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
                }
            }
            
            logger.warn("Backend returned null for products by category");
            return new PageImpl<>(List.of(), PageRequest.of(page, size), 0);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Category {} not found", categoryId);
                throw new EntityNotFoundException("Categoria", categoryId.toString());
            }
            
            String errorMsg = String.format("Backend API error while retrieving products by category %s: %s - %s", 
                    categoryId, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = String.format("Error retrieving products by category %s: %s", 
                    categoryId, e.getMessage());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Validates pagination parameters.
     * 
     * @param page the page number (must be >= 0)
     * @param size the page size (must be between 1 and 100)
     * @throws IllegalArgumentException if parameters are invalid
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Page size must be at least 1");
        }
        if (size > 100) {
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }
    }

    /**
     * Validates conditional precioVenta requirement.
     * When precioPersonalizado is true, precioVenta must be provided and positive.
     * 
     * @param productDTO the product DTO to validate
     * @throws ValidationException if validation fails
     */
    private void validateConditionalPrecioVenta(ProductDTO productDTO) {
        if (!productDTO.isValidPrecioVenta()) {
            String errorMsg = productDTO.getPrecioVentaValidationError();
            logger.debug("Validation error for conditional precioVenta: {}", errorMsg);
            throw new ValidationException(errorMsg);
        }
    }

    /**
     * Invalidates products-related cache entries.
     * Since products are mutable, we don't cache them but this method
     * ensures consistency if caching is added in the future.
     */
    private void invalidateProductsCache() {
        logger.debug("Invalidating products cache (if any)");
        
        // Currently products are not cached due to mutability
        // but this method provides cache invalidation pattern for future use
        cacheManager.invalidateByPattern(PRODUCT_CACHE_KEY_PREFIX + "*");
    }

    /**
     * Handles errors from write operations (create, update) with proper exception mapping.
     * 
     * @param e the WebClientResponseException
     * @param operation the operation being performed ("create" or "update")
     * @param productName the product name for context
     * @throws ConflictException for 409 status (duplicate name)
     * @throws ValidationException for 400 status (validation errors)
     * @throws BusinessRuleException for 422 status (business rule violations)
     * @throws RuntimeException for other errors
     */
    private void handleWriteOperationError(WebClientResponseException e, String operation, String productName) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        
        switch (statusCode) {
            case 409:
                logger.debug("Conflict error during product {}: duplicate name '{}'", operation, productName);
                throw new ConflictException("Ya existe un producto con el nombre '" + productName + "'");
                
            case 400:
                logger.debug("Validation error during product {}: {}", operation, responseBody);
                throw new ValidationException("Datos inválidos para el producto: " + responseBody);
                
            case 422:
                logger.debug("Business rule violation during product {}: {}", operation, responseBody);
                throw new BusinessRuleException("Regla de negocio violada: " + responseBody);
                
            default:
                String errorMsg = String.format("Backend API error during product %s: %s - %s", 
                        operation, e.getStatusCode(), responseBody);
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Handles errors from IA identification operations with proper exception mapping.
     * 
     * @param e the WebClientResponseException
     * @param imagenBase64 the base64 image data for context
     * @throws BusinessRuleException for 429 status (quota exceeded)
     * @throws ValidationException for 400 status (invalid image format)
     * @throws RuntimeException for other errors
     */
    private void handleIAIdentificationError(WebClientResponseException e, String imagenBase64) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        
        switch (statusCode) {
            case 429:
                logger.debug("IA quota exceeded for image identification");
                throw new BusinessRuleException("Cuota mensual de IA agotada. Intente nuevamente el próximo mes.");
                
            case 400:
                logger.debug("Invalid image format for IA identification: {}", responseBody);
                throw new ValidationException("Formato de imagen no soportado. Use JPG, PNG, GIF o WebP.");
                
            case 422:
                logger.debug("IA service business rule violation: {}", responseBody);
                throw new BusinessRuleException("No se pudo procesar la imagen: " + responseBody);
                
            default:
                String errorMsg = String.format("Backend API error during IA identification: %s - %s", 
                        e.getStatusCode(), responseBody);
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Converts a ProductPageResponse from backend to a Spring Data Page.
     * 
     * @param response the backend page response
     * @return Spring Data Page
     */
    private Page<ProductDTO> convertToPage(ProductPageResponse response) {
        return new PageImpl<>(
            response.getContent(),
            PageRequest.of(response.getNumber(), response.getSize()),
            response.getTotalElements()
        );
    }

    /**
     * Response wrapper for paginated product data from backend.
     */
    private static class ProductPageResponse {
        private List<ProductDTO> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        // Getters and setters
        public List<ProductDTO> getContent() { return content; }
        public void setContent(List<ProductDTO> content) { this.content = content; }
        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }

}