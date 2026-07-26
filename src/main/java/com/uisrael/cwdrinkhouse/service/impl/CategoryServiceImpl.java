package com.uisrael.cwdrinkhouse.service.impl;

import com.uisrael.cwdrinkhouse.dto.CategoryDTO;
import com.uisrael.cwdrinkhouse.exception.BusinessRuleException;
import com.uisrael.cwdrinkhouse.exception.ConflictException;
import com.uisrael.cwdrinkhouse.exception.EntityNotFoundException;
import com.uisrael.cwdrinkhouse.exception.ValidationException;
import com.uisrael.cwdrinkhouse.service.CacheManager;
import com.uisrael.cwdrinkhouse.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of CategoryService using WebClient for backend API communication.
 * Provides complete CRUD operations with caching and error handling.
 * 
 * Features:
 * - WebClient integration for REST API calls to localhost:8080
 * - Caching layer using CacheManager with configurable TTL
 * - Cache invalidation on write operations (create, update, delete)
 * - Comprehensive error handling with proper HTTP status code mapping
 * - Retry logic for transient failures
 * - Logging and monitoring
 * 
 * Requirements: 2.1-2.8, 18.1, 18.5-18.6
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    
    private static final String CATEGORIES_ENDPOINT = "/api/v1/categorias";
    private static final String CATEGORY_BY_ID_ENDPOINT = "/api/v1/categorias/{id}";
    private static final String CATEGORIES_CACHE_KEY = "categories:all";
    private static final String CATEGORY_CACHE_KEY_PREFIX = "category:";

    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final Retry retryConfiguration;

    @Autowired
    public CategoryServiceImpl(WebClient webClient, CacheManager cacheManager, Retry retryConfiguration) {
        this.webClient = webClient;
        this.cacheManager = cacheManager;
        this.retryConfiguration = retryConfiguration;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        logger.debug("Retrieving all categories");
        
        // Try to get from cache first
        List<CategoryDTO> cachedCategories = cacheManager.getCategory(CATEGORIES_CACHE_KEY);
        if (cachedCategories != null) {
            logger.debug("Retrieved {} categories from cache", cachedCategories.size());
            return cachedCategories;
        }

        // Not in cache, fetch from backend
        try {
            // First try to get as paginated response
            List<CategoryDTO> categories;
            try {
                CategoryPageResponse pageResponse = webClient.get()
                        .uri(CATEGORIES_ENDPOINT)
                        .retrieve()
                        .bodyToMono(CategoryPageResponse.class)
                        .retryWhen(retryConfiguration)
                        .block();
                
                categories = (pageResponse != null) ? pageResponse.getContent() : null;
                logger.debug("Retrieved paginated response with {} categories", 
                        (categories != null) ? categories.size() : 0);
                
            } catch (Exception e) {
                // If paginated fails, try as direct array
                logger.debug("Paginated fetch failed, trying direct array: {}", e.getMessage());
                categories = webClient.get()
                        .uri(CATEGORIES_ENDPOINT)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<CategoryDTO>>() {})
                        .retryWhen(retryConfiguration)
                        .block();
            }

            if (categories != null) {
                // Cache the result
                cacheManager.putCategory(CATEGORIES_CACHE_KEY, categories);
                
                // Also cache individual categories
                categories.forEach(category -> 
                    cacheManager.putCategory(CATEGORY_CACHE_KEY_PREFIX + category.getCategoriaId(), category)
                );
                
                logger.info("Retrieved and cached {} categories from backend", categories.size());
                return categories;
            } else {
                logger.warn("Backend returned null for categories list");
                return List.of(); // Return empty list instead of null
            }
            
        } catch (WebClientResponseException e) {
            String errorMsg = String.format("Backend API error while retrieving categories: %s - %s", 
                    e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Error retrieving categories from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        logger.debug("Retrieving category with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        // Try to get from cache first
        String cacheKey = CATEGORY_CACHE_KEY_PREFIX + id;
        CategoryDTO cachedCategory = cacheManager.getCategory(cacheKey);
        if (cachedCategory != null) {
            logger.debug("Retrieved category {} from cache", id);
            return cachedCategory;
        }

        // Not in cache, fetch from backend
        try {
            CategoryDTO category = webClient.get()
                    .uri(CATEGORY_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (category != null) {
                // Cache the result
                cacheManager.putCategory(cacheKey, category);
                logger.info("Retrieved and cached category {}", id);
                return category;
            } else {
                throw new EntityNotFoundException("Categoria", id.toString());
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Category {} not found in backend", id);
                throw new EntityNotFoundException("Categoria", id.toString());
            }
            
            String errorMsg = String.format("Backend API error while retrieving category %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error retrieving category " + id + " from backend: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        logger.debug("Creating new category: {}", categoryDTO.getNombre());
        
        if (categoryDTO == null) {
            throw new IllegalArgumentException("CategoryDTO cannot be null");
        }
        
        // Ensure ID is null for creation
        categoryDTO.setCategoriaId(null);

        try {
            CategoryDTO createdCategory = webClient.post()
                    .uri(CATEGORIES_ENDPOINT)
                    .bodyValue(categoryDTO)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (createdCategory != null) {
                // Invalidate cache since we have new data
                invalidateCategoriesCache();
                
                // Cache the new category
                cacheManager.putCategory(CATEGORY_CACHE_KEY_PREFIX + createdCategory.getCategoriaId(), createdCategory);
                
                logger.info("Successfully created category with ID: {}", createdCategory.getCategoriaId());
                return createdCategory;
            } else {
                throw new RuntimeException("Backend returned null after category creation");
            }
            
        } catch (WebClientResponseException e) {
            handleWriteOperationError(e, "create", categoryDTO.getNombre());
            return null; // This line will never be reached due to exception throwing above
        } catch (Exception e) {
            String errorMsg = "Error creating category: " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        logger.debug("Updating category with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }
        if (categoryDTO == null) {
            throw new IllegalArgumentException("CategoryDTO cannot be null");
        }
        
        // Ensure the ID matches
        categoryDTO.setCategoriaId(id);

        try {
            CategoryDTO updatedCategory = webClient.put()
                    .uri(CATEGORY_BY_ID_ENDPOINT, id)
                    .bodyValue(categoryDTO)
                    .retrieve()
                    .bodyToMono(CategoryDTO.class)
                    .retryWhen(retryConfiguration)
                    .block();

            if (updatedCategory != null) {
                // Invalidate cache since we have updated data
                invalidateCategoriesCache();
                
                // Update individual category cache
                cacheManager.putCategory(CATEGORY_CACHE_KEY_PREFIX + id, updatedCategory);
                
                logger.info("Successfully updated category with ID: {}", id);
                return updatedCategory;
            } else {
                throw new RuntimeException("Backend returned null after category update");
            }
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Category {} not found for update", id);
                throw new EntityNotFoundException("Categoria", id.toString());
            }
            
            handleWriteOperationError(e, "update", categoryDTO.getNombre());
            return null; // This line will never be reached due to exception throwing above
        } catch (EntityNotFoundException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error updating category " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public void deleteCategory(Long id) {
        logger.debug("Deleting category with ID: {}", id);
        
        if (id == null) {
            throw new IllegalArgumentException("Category ID cannot be null");
        }

        try {
            webClient.delete()
                    .uri(CATEGORY_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .retryWhen(retryConfiguration)
                    .block();

            // Invalidate cache since category was deleted
            invalidateCategoriesCache();
            
            // Remove individual category from cache
            cacheManager.remove(CATEGORY_CACHE_KEY_PREFIX + id);
            
            logger.info("Successfully deleted category with ID: {}", id);
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Category {} not found for deletion", id);
                throw new EntityNotFoundException("Categoria", id.toString());
            }
            if (e.getStatusCode().value() == 422) {
                logger.debug("Category {} cannot be deleted due to business rules", id);
                throw new BusinessRuleException("No se puede eliminar la categoría porque tiene productos asociados");
            }
            
            String errorMsg = String.format("Backend API error while deleting category %s: %s - %s", 
                    id, e.getStatusCode(), e.getResponseBodyAsString());
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (EntityNotFoundException | BusinessRuleException e) {
            throw e; // Re-throw as-is
        } catch (Exception e) {
            String errorMsg = "Error deleting category " + id + ": " + e.getMessage();
            logger.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    @Override
    public boolean categoryExists(Long id) {
        logger.debug("Checking if category exists: {}", id);
        
        if (id == null) {
            return false;
        }

        // Check cache first
        String cacheKey = CATEGORY_CACHE_KEY_PREFIX + id;
        if (cacheManager.containsKey(cacheKey)) {
            logger.debug("Category {} exists in cache", id);
            return true;
        }

        // Make lightweight HEAD request to backend
        try {
            webClient.head()
                    .uri(CATEGORY_BY_ID_ENDPOINT, id)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
            
            logger.debug("Category {} exists in backend", id);
            return true;
            
        } catch (WebClientResponseException e) {
            if (e.getStatusCode().value() == 404) {
                logger.debug("Category {} does not exist", id);
                return false;
            }
            
            // For other errors, assume it exists to be safe
            logger.warn("Error checking category existence for {}: {}", id, e.getMessage());
            return true;
        } catch (Exception e) {
            logger.warn("Error checking category existence for {}: {}", id, e.getMessage());
            return true; // Assume exists on error to be safe
        }
    }

    @Override
    public List<CategoryDTO> refreshCategoriesCache() {
        logger.debug("Refreshing categories cache");
        
        // Clear cache first
        clearCache();
        
        // Fetch fresh data
        return getAllCategories();
    }

    @Override
    public void clearCache() {
        logger.debug("Clearing categories cache");
        
        // Invalidate all categories cache
        cacheManager.invalidateCategories();
        
        // Also clear individual category cache entries
        cacheManager.invalidateByPattern(CATEGORY_CACHE_KEY_PREFIX + "*");
    }

    @Override
    public Map<String, Object> getCacheStatistics() {
        logger.debug("Retrieving cache statistics");
        
        Map<String, Object> stats = new HashMap<>(cacheManager.getStatistics());
        
        // Add category-specific statistics
        stats.put("categoriesCacheSize", cacheManager.categoriesCacheSize());
        stats.put("allCategoriesCached", cacheManager.containsKey(CATEGORIES_CACHE_KEY));
        
        return stats;
    }

    /**
     * Invalidates all categories-related cache entries.
     * Call this after any write operation (create, update, delete).
     */
    private void invalidateCategoriesCache() {
        logger.debug("Invalidating categories cache");
        
        // Remove the main categories list from cache
        cacheManager.remove(CATEGORIES_CACHE_KEY);
        
        // Invalidate all categories caches managed by CacheManager
        cacheManager.invalidateCategories();
    }

    /**
     * Handles errors from write operations (create, update) with proper exception mapping.
     * 
     * @param e the WebClientResponseException
     * @param operation the operation being performed ("create" or "update")
     * @param categoryName the category name for context
     * @throws ConflictException for 409 status (duplicate name)
     * @throws ValidationException for 400 status (validation errors)
     * @throws RuntimeException for other errors
     */
    private void handleWriteOperationError(WebClientResponseException e, String operation, String categoryName) {
        int statusCode = e.getStatusCode().value();
        String responseBody = e.getResponseBodyAsString();
        
        switch (statusCode) {
            case 409:
                logger.debug("Conflict error during category {}: duplicate name '{}'", operation, categoryName);
                throw new ConflictException("Ya existe una categoría con el nombre '" + categoryName + "'");
                
            case 400:
                logger.debug("Validation error during category {}: {}", operation, responseBody);
                throw new ValidationException("Datos inválidos para la categoría: " + responseBody);
                
            case 422:
                logger.debug("Business rule violation during category {}: {}", operation, responseBody);
                throw new BusinessRuleException("Regla de negocio violada: " + responseBody);
                
            default:
                String errorMsg = String.format("Backend API error during category %s: %s - %s", 
                        operation, e.getStatusCode(), responseBody);
                logger.error(errorMsg, e);
                throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Response wrapper for paginated category data from backend.
     */
    private static class CategoryPageResponse {
        private List<CategoryDTO> content;
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        // Getters and setters
        public List<CategoryDTO> getContent() { return content; }
        public void setContent(List<CategoryDTO> content) { this.content = content; }
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