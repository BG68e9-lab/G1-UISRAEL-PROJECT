package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.ProviderDTO;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service interface for Provider management operations.
 * Provides methods for CRUD operations, validation, and search functionality.
 * 
 * Requirements: 4.1-4.9, 14.1-14.9, 18.1-18.8
 */
public interface ProviderService {

    /**
     * Retrieves all providers with pagination.
     * 
     * @param page the page number (0-based)
     * @param size the number of providers per page
     * @return paginated list of providers
     */
    Page<ProviderDTO> getAllProviders(int page, int size);

    /**
     * Retrieves all providers as a list.
     * Used for dropdowns and selections.
     * 
     * @return list of all active providers
     */
    List<ProviderDTO> getAllProviders();

    /**
     * Retrieves a single provider by its ID.
     * 
     * @param id the provider ID
     * @return the provider DTO
     * @throws EntityNotFoundException if provider with given ID doesn't exist
     */
    ProviderDTO getProviderById(Long id);

    /**
     * Creates a new provider.
     * Validates RUC format and uniqueness before creation.
     * 
     * @param providerDTO the provider data to create
     * @return the created provider with generated ID and timestamps
     * @throws ConflictException if RUC already exists (409)
     * @throws ValidationException if RUC format is invalid or email format is invalid
     * 
     * Requirements: 4.3, 4.6, 4.7
     */
    ProviderDTO createProvider(ProviderDTO providerDTO);

    /**
     * Updates an existing provider.
     * Validates RUC format and uniqueness (excluding current provider) before update.
     * 
     * @param id the provider ID to update
     * @param providerDTO the updated provider data
     * @return the updated provider
     * @throws EntityNotFoundException if provider with given ID doesn't exist
     * @throws ConflictException if updated RUC conflicts with existing provider (409)
     * @throws ValidationException if RUC format is invalid or email format is invalid
     * 
     * Requirements: 4.5
     */
    ProviderDTO updateProvider(Long id, ProviderDTO providerDTO);

    /**
     * Deletes a provider by ID.
     * 
     * @param id the provider ID to delete
     * @throws EntityNotFoundException if provider with given ID doesn't exist
     * @throws BusinessRuleException if provider cannot be deleted due to dependencies (422)
     */
    void deleteProvider(Long id);

    /**
     * Searches providers by razón social and/or RUC.
     * Both parameters are optional and support partial matching.
     * 
     * @param razonSocial the company name to search (optional, case-insensitive contains)
     * @param ruc the RUC to search (optional, exact or partial match)
     * @return list of providers matching search criteria
     * 
     * Requirements: 4.8
     */
    List<ProviderDTO> searchProviders(String razonSocial, String ruc);

    /**
     * Searches providers with pagination.
     * 
     * @param razonSocial the company name to search (optional)
     * @param ruc the RUC to search (optional)
     * @param page the page number (0-based)
     * @param size the number of providers per page
     * @return paginated list of providers matching search criteria
     */
    Page<ProviderDTO> searchProviders(String razonSocial, String ruc, int page, int size);

    /**
     * Validates RUC format according to Ecuador standards.
     * Performs format validation (13 digits) and basic checksum validation.
     * 
     * @param ruc the RUC to validate
     * @return true if RUC format is valid
     * 
     * Requirements: 4.7
     */
    boolean validateRucFormat(String ruc);

    /**
     * Checks if a RUC is unique in the system.
     * 
     * @param ruc the RUC to check
     * @param excludeId optional provider ID to exclude from uniqueness check (for updates)
     * @return true if RUC is unique
     * 
     * Requirements: 4.6
     */
    boolean isRucUnique(String ruc, Long excludeId);

    /**
     * Validates email format.
     * 
     * @param email the email to validate
     * @return true if email format is valid
     * 
     * Requirements: 4.9
     */
    boolean validateEmailFormat(String email);

    /**
     * Retrieves providers by active status.
     * 
     * @param activo true for active providers, false for inactive
     * @return list of providers with specified active status
     */
    List<ProviderDTO> getProvidersByActiveStatus(boolean activo);

    /**
     * Activates or deactivates a provider.
     * 
     * @param id the provider ID
     * @param activo new active status
     * @return the updated provider
     * @throws EntityNotFoundException if provider with given ID doesn't exist
     */
    ProviderDTO updateProviderActiveStatus(Long id, boolean activo);
}