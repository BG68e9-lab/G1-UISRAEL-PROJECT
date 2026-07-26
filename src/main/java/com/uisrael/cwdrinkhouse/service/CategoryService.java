package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.CategoryDTO;
import java.util.List;

/**
 * Service interface for managing categories.
 * Provides CRUD operations for categories with caching support.
 * Handles communication with backend API at localhost:8080.
 * 
 * Features:
 * - Complete CRUD operations (getAllCategories, getCategoryById, createCategory, updateCategory, deleteCategory)
 * - Caching layer for read operations (LocalStorage via template JS)
 * - Cache invalidation on write operations (create, update, delete)
 * - Error handling for 409 Conflict (duplicate names) and 404 Not Found
 * - WebClient integration for REST API communication
 * 
 * Requirements: 2.1-2.8, 18.1, 18.5-18.6
 */
public interface CategoryService {

    /**
     * Retrieve all categories from the backend API.
     * Uses caching with configurable TTL. If cached data is available and not expired,
     * returns cached results. Otherwise, fetches from backend and updates cache.
     * 
     * @return List of all CategoryDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<CategoryDTO> getAllCategories();

    /**
     * Retrieve a specific category by its ID.
     * First checks cache, then queries backend API if not found or expired.
     * 
     * @param id the category ID
     * @return CategoryDTO for the specified ID
     * @throws EntityNotFoundException if category with the given ID does not exist (404)
     * @throws RuntimeException if backend API call fails
     */
    CategoryDTO getCategoryById(Long id);

    /**
     * Create a new category in the backend.
     * Validates the categoryDTO, sends to backend API, and invalidates cache on success.
     * 
     * @param categoryDTO the category data to create (categoriaId should be null)
     * @return CategoryDTO with the created category including generated ID and timestamps
     * @throws ConflictException if category name already exists (409)
     * @throws ValidationException if categoryDTO validation fails (400)
     * @throws RuntimeException if backend API call fails
     */
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    /**
     * Update an existing category in the backend.
     * Validates the categoryDTO, sends update to backend API, and invalidates cache on success.
     * 
     * @param id the ID of the category to update
     * @param categoryDTO the updated category data
     * @return CategoryDTO with the updated category data
     * @throws EntityNotFoundException if category with the given ID does not exist (404)
     * @throws ConflictException if updated name conflicts with existing category (409)
     * @throws ValidationException if categoryDTO validation fails (400)
     * @throws RuntimeException if backend API call fails
     */
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * Delete a category from the backend.
     * Attempts to delete the category and invalidates cache on success.
     * 
     * @param id the ID of the category to delete
     * @throws EntityNotFoundException if category with the given ID does not exist (404)
     * @throws BusinessRuleException if category cannot be deleted due to business rules (422)
     *         e.g., category has associated products
     * @throws RuntimeException if backend API call fails
     */
    void deleteCategory(Long id);

    /**
     * Check if a category exists by ID.
     * Optimized method that checks cache first, then makes a lightweight backend call.
     * 
     * @param id the category ID to check
     * @return true if the category exists, false otherwise
     */
    boolean categoryExists(Long id);

    /**
     * Refresh the categories cache.
     * Forces a reload of all categories from backend and updates cache.
     * Useful when cache may be stale or after bulk operations.
     * 
     * @return List of refreshed CategoryDTO objects
     * @throws RuntimeException if backend API call fails
     */
    List<CategoryDTO> refreshCategoriesCache();

    /**
     * Clear all categories from cache.
     * Invalidates all cached category data, forcing fresh retrieval on next access.
     */
    void clearCache();

    /**
     * Get cache statistics for categories.
     * Provides information about cache usage, hit ratios, and configuration.
     * 
     * @return Map containing cache statistics (size, hits, misses, TTL, etc.)
     */
    java.util.Map<String, Object> getCacheStatistics();
}