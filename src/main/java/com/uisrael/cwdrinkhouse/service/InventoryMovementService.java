package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.InventoryMovementDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing inventory movements.
 * Handles ENTRADA, SALIDA, and AJUSTE movement types with type-specific validation.
 * 
 * Requirements: 7.1-7.12, 18.5-18.6
 */
public interface InventoryMovementService {

    /**
     * Retrieves all inventory movements with pagination.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of inventory movements
     */
    Page<InventoryMovementDTO> getAllMovements(int page, int size);

    /**
     * Retrieves inventory movements filtered by type with pagination.
     * 
     * @param tipo the movement type (ENTRADA, SALIDA, AJUSTE)
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of filtered inventory movements
     */
    Page<InventoryMovementDTO> getMovementsByType(String tipo, int page, int size);

    /**
     * Retrieves inventory movements within a date range.
     * 
     * @param fechaInicio the start date (inclusive)
     * @param fechaFin the end date (inclusive)
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of movements within date range
     */
    Page<InventoryMovementDTO> getMovementsByDateRange(LocalDateTime fechaInicio, 
                                                      LocalDateTime fechaFin, 
                                                      int page, int size);

    /**
     * Retrieves inventory movements filtered by type and date range.
     * 
     * @param tipo the movement type (optional)
     * @param fechaInicio the start date (optional)
     * @param fechaFin the end date (optional)
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of filtered movements
     */
    Page<InventoryMovementDTO> getMovementsWithFilters(String tipo, 
                                                      LocalDateTime fechaInicio, 
                                                      LocalDateTime fechaFin, 
                                                      int page, int size);

    /**
     * Retrieves a specific inventory movement by ID.
     * 
     * @param movimientoId the movement ID
     * @return the inventory movement DTO
     * @throws EntityNotFoundException if movement not found
     */
    InventoryMovementDTO getMovementById(Long movimientoId);

    /**
     * Creates a new inventory movement with type-specific validation.
     * Validates business rules based on movement type:
     * - ENTRADA: requires loteId, positive cantidad, positive precioUnitario
     * - SALIDA: requires loteId, positive cantidad, validates against available stock
     * - AJUSTE: requires non-zero cantidad (can be negative)
     * 
     * @param movementDTO the movement data
     * @return the created movement with generated codigoMovimiento
     * @throws BusinessRuleException if validation fails (HTTP 422)
     */
    InventoryMovementDTO createMovement(InventoryMovementDTO movementDTO);

    /**
     * Updates an existing inventory movement.
     * 
     * @param movimientoId the movement ID
     * @param movementDTO the updated movement data
     * @return the updated movement
     * @throws EntityNotFoundException if movement not found
     * @throws BusinessRuleException if validation fails
     */
    InventoryMovementDTO updateMovement(Long movimientoId, InventoryMovementDTO movementDTO);

    /**
     * Deletes an inventory movement by ID.
     * 
     * @param movimientoId the movement ID
     * @throws EntityNotFoundException if movement not found
     */
    void deleteMovement(Long movimientoId);

    /**
     * Retrieves movements for a specific product.
     * 
     * @param productoId the product ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of movements for the product
     */
    Page<InventoryMovementDTO> getMovementsByProduct(Long productoId, int page, int size);

    /**
     * Retrieves movements for a specific lot.
     * 
     * @param loteId the lot ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of movements for the lot
     */
    Page<InventoryMovementDTO> getMovementsByLot(Long loteId, int page, int size);

    /**
     * Generates a movement code based on type and timestamp.
     * Format: 
     * - ENT-YYYYMMDD-HHMMSS (ENTRADA)
     * - SAL-YYYYMMDD-HHMMSS (SALIDA)
     * - AJU-YYYYMMDD-HHMMSS (AJUSTE)
     * 
     * @param tipo the movement type
     * @return the generated movement code
     */
    String generateMovementCode(String tipo);

    /**
     * Validates SALIDA movement against available stock.
     * 
     * @param productoId the product ID
     * @param loteId the lot ID
     * @param cantidad the requested quantity
     * @return true if sufficient stock available
     */
    boolean validateStockAvailability(Long productoId, Long loteId, Integer cantidad);

    /**
     * Gets available stock for a specific product lot.
     * 
     * @param productoId the product ID
     * @param loteId the lot ID (optional, null for total product stock)
     * @return available stock quantity
     */
    Integer getAvailableStock(Long productoId, Long loteId);
}