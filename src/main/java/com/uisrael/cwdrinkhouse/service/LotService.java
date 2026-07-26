package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.LotDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing product lots in inventory.
 * Handles lot tracking, expiration monitoring, and inventory management.
 * 
 * Requirements: 6.1-6.7, 18.5-18.6
 */
public interface LotService {

    /**
     * Get all lots with pagination support.
     * Returns lots ordered by fechaIngreso (FIFO - First In, First Out).
     * 
     * @param pageable pagination information
     * @return page of LotDTO objects
     */
    Page<LotDTO> getAllLots(Pageable pageable);

    /**
     * Get all lots without pagination.
     * Returns lots ordered by fechaIngreso (FIFO).
     * 
     * @return list of all LotDTO objects
     */
    List<LotDTO> getAllLots();

    /**
     * Get lots filtered by product ID.
     * Returns lots for a specific product ordered by fechaIngreso (FIFO).
     * 
     * @param productoId the product ID to filter by
     * @return list of LotDTO objects for the specified product
     */
    List<LotDTO> getLotsByProduct(Long productoId);

    /**
     * Get lots filtered by product ID with pagination.
     * Returns lots for a specific product ordered by fechaIngreso (FIFO).
     * 
     * @param productoId the product ID to filter by
     * @param pageable pagination information
     * @return page of LotDTO objects for the specified product
     */
    Page<LotDTO> getLotsByProduct(Long productoId, Pageable pageable);

    /**
     * Get lots expiring within the specified number of days.
     * Default implementation uses 7-day window as per requirements.
     * 
     * @param days number of days from now to check for expiration
     * @return list of LotDTO objects expiring within the specified days
     */
    List<LotDTO> getLotsExpiringInDays(int days);

    /**
     * Get lots expiring within 7 days (proximity-to-expiration detection).
     * This is the default expiration window as specified in requirements.
     * 
     * @return list of LotDTO objects expiring within 7 days
     */
    default List<LotDTO> getLotsExpiringSoon() {
        return getLotsExpiringInDays(7);
    }

    /**
     * Get lots expiring within the specified number of days with pagination.
     * 
     * @param days number of days from now to check for expiration
     * @param pageable pagination information
     * @return page of LotDTO objects expiring within the specified days
     */
    Page<LotDTO> getLotsExpiringInDays(int days, Pageable pageable);

    /**
     * Get lots expiring between specific dates.
     * 
     * @param startDate start of the date range (inclusive)
     * @param endDate end of the date range (inclusive)
     * @return list of LotDTO objects expiring within the date range
     */
    List<LotDTO> getLotsExpiringBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Get a specific lot by its ID.
     * 
     * @param loteId the lot ID
     * @return Optional containing LotDTO if found, empty otherwise
     */
    Optional<LotDTO> getLotById(Long loteId);

    /**
     * Get lots by entry code (codigoEntrada).
     * 
     * @param codigoEntrada the entry code to search for
     * @return Optional containing LotDTO if found, empty otherwise
     */
    Optional<LotDTO> getLotByEntryCode(String codigoEntrada);

    /**
     * Get active lots only (activo = true).
     * 
     * @return list of active LotDTO objects
     */
    List<LotDTO> getActiveLots();

    /**
     * Get lots with available stock (cantidadDisponible > 0).
     * 
     * @return list of LotDTO objects with available stock
     */
    List<LotDTO> getLotsWithAvailableStock();

    /**
     * Get lots with available stock for a specific product.
     * Useful for inventory management and sales processing.
     * 
     * @param productoId the product ID
     * @return list of LotDTO objects with available stock for the product
     */
    List<LotDTO> getLotsWithAvailableStock(Long productoId);

    /**
     * Create a new lot.
     * 
     * @param lotDTO the lot data to create
     * @return the created LotDTO with generated ID and entry code
     */
    LotDTO createLot(LotDTO lotDTO);

    /**
     * Update an existing lot.
     * 
     * @param loteId the lot ID to update
     * @param lotDTO the updated lot data
     * @return the updated LotDTO
     */
    LotDTO updateLot(Long loteId, LotDTO lotDTO);

    /**
     * Update the available quantity of a lot.
     * This is typically called after inventory movements.
     * 
     * @param loteId the lot ID
     * @param newQuantity the new available quantity
     * @return the updated LotDTO
     */
    LotDTO updateLotQuantity(Long loteId, Integer newQuantity);

    /**
     * Deactivate a lot (set activo = false).
     * Deactivated lots are not considered for inventory operations.
     * 
     * @param loteId the lot ID to deactivate
     * @return the updated LotDTO
     */
    LotDTO deactivateLot(Long loteId);

    /**
     * Activate a lot (set activo = true).
     * 
     * @param loteId the lot ID to activate
     * @return the updated LotDTO
     */
    LotDTO activateLot(Long loteId);

    /**
     * Delete a lot permanently.
     * Should only be allowed if the lot has no associated inventory movements.
     * 
     * @param loteId the lot ID to delete
     * @throws BusinessRuleException if lot has associated movements or transactions
     */
    void deleteLot(Long loteId);

    /**
     * Get the count of lots expiring within the specified days.
     * Useful for dashboard alerts and notifications.
     * 
     * @param days number of days from now to check for expiration
     * @return count of expiring lots
     */
    long getExpiringLotsCount(int days);

    /**
     * Get the count of lots expiring within 7 days.
     * 
     * @return count of lots expiring soon
     */
    default long getExpiringLotsCount() {
        return getExpiringLotsCount(7);
    }

    /**
     * Get total count of lots.
     * 
     * @return total number of lots
     */
    long getTotalLotsCount();

    /**
     * Get count of active lots.
     * 
     * @return count of active lots
     */
    long getActiveLotsCount();

    /**
     * Get count of lots with available stock.
     * 
     * @return count of lots with stock > 0
     */
    long getLotsWithStockCount();

    /**
     * Search lots by product name, brand, or type.
     * Performs text search across product-related fields.
     * 
     * @param searchTerm the search term to look for
     * @param pageable pagination information
     * @return page of LotDTO objects matching the search
     */
    Page<LotDTO> searchLots(String searchTerm, Pageable pageable);

    /**
     * Get lots filtered by multiple criteria.
     * 
     * @param productoId product ID filter (optional)
     * @param expiringInDays expiration filter in days (optional)
     * @param hasStock filter by available stock (optional)
     * @param isActive filter by active status (optional)
     * @param pageable pagination information
     * @return page of LotDTO objects matching the filters
     */
    Page<LotDTO> getLotsWithFilters(Long productoId, Integer expiringInDays, Boolean hasStock, 
                                   Boolean isActive, Pageable pageable);

    /**
     * Invalidate cache for lot-related data.
     * Called after lot create, update, or delete operations.
     */
    void invalidateCache();
}