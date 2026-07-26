package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.ProductDTO;
import com.uisrael.cwdrinkhouse.dto.IAIdentificationResultDTO;
import org.springframework.data.domain.Page;

/**
 * Service interface for Product management operations.
 * Provides methods for CRUD operations, pagination, search filtering, and IA identification.
 * 
 * Requirements: 3.2-3.12, 12.3-12.7, 18.5-18.6
 */
public interface ProductService {

    /**
     * Retrieves a paginated list of all products.
     * 
     * @param page the page number (0-based)
     * @param size the number of products per page
     * @return paginated list of products
     */
    Page<ProductDTO> getProducts(int page, int size);

    /**
     * Retrieves a paginated list of products with simple search functionality.
     * Searches across nombre, marca, and tipo fields using OR logic.
     * 
     * @param page the page number (0-based)
     * @param size the number of products per page
     * @param search the search term (optional, searches in nombre, marca, tipo)
     * @return paginated list of products matching search criteria
     * 
     * Requirements: 3.1, 3.2
     */
    Page<ProductDTO> getProducts(int page, int size, String search);

    /**
     * Searches products with optional filters for nombre, marca, and tipo.
     * All provided filters are applied with AND logic.
     * 
     * @param nombre product name filter (optional, case-insensitive contains)
     * @param marca product brand filter (optional, case-insensitive contains)
     * @param tipo product type filter (optional, case-insensitive contains)
     * @param page the page number (0-based)
     * @param size the number of products per page
     * @return paginated list of filtered products
     * 
     * Requirements: 3.2
     */
    Page<ProductDTO> searchProducts(String nombre, String marca, String tipo, int page, int size);

    /**
     * Retrieves a single product by its ID.
     * 
     * @param id the product ID
     * @return the product DTO
     * @throws EntityNotFoundException if product with given ID doesn't exist
     */
    ProductDTO getProductById(Long id);

    /**
     * Creates a new product.
     * Validates business rules including duplicate name detection.
     * 
     * @param productDTO the product data to create
     * @return the created product with generated ID and timestamps
     * @throws ConflictException if product name already exists (409)
     * @throws BusinessRuleException if business validation fails (422)
     * 
     * Requirements: 3.6, 3.9
     */
    ProductDTO createProduct(ProductDTO productDTO);

    /**
     * Updates an existing product.
     * Validates business rules including duplicate name detection.
     * 
     * @param id the product ID to update
     * @param productDTO the updated product data
     * @return the updated product
     * @throws EntityNotFoundException if product with given ID doesn't exist
     * @throws ConflictException if updated name conflicts with existing product (409)
     * @throws BusinessRuleException if business validation fails (422)
     * 
     * Requirements: 3.8
     */
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    /**
     * Deletes a product by ID.
     * 
     * @param id the product ID to delete
     * @throws EntityNotFoundException if product with given ID doesn't exist
     * @throws BusinessRuleException if product cannot be deleted due to dependencies (422)
     */
    void deleteProduct(Long id);

    /**
     * Identifies a product using AI image analysis.
     * Sends base64-encoded image to backend AI service for processing.
     * 
     * @param imagenBase64 the base64-encoded image data
     * @return the AI identification result with confidence level and product details
     * @throws BusinessRuleException if monthly IA quota is exceeded (429)
     * @throws ValidationException if image format is not supported
     * 
     * Requirements: 12.3-12.7
     */
    IAIdentificationResultDTO identifyProductByIA(String imagenBase64);

    /**
     * Retrieves products by category with pagination.
     * 
     * @param categoryId the category ID to filter by
     * @param page the page number (0-based)
     * @param size the number of products per page
     * @return paginated list of products in the specified category
     * @throws EntityNotFoundException if category with given ID doesn't exist
     * 
     * Requirements: 3.2
     */
    Page<ProductDTO> getProductsByCategory(Long categoryId, int page, int size);
}