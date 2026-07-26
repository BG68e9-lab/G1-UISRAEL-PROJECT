package com.uisrael.cwdrinkhouse.service;

import com.uisrael.cwdrinkhouse.dto.OrderDTO;
import com.uisrael.cwdrinkhouse.dto.OrderDetailDTO;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for Order management operations.
 * Provides CRUD operations and state transition management for purchase orders.
 * 
 * Requirements: 5.1-5.14, 18.5-18.6
 */
public interface OrderService {

    /**
     * Retrieves all orders with pagination and optional filtering.
     * 
     * @param page the page number (0-based)
     * @param size the page size
     * @param estado optional state filter (BORRADOR, ENVIADA, RECIBIDA, ANULADA)
     * @return page of orders matching criteria
     */
    Page<OrderDTO> getAllOrders(int page, int size, String estado);

    /**
     * Retrieves all orders with pagination and comprehensive filtering.
     * 
     * @param estado optional state filter (BORRADOR, ENVIADA, RECIBIDA, ANULADA)
     * @param fechaDesde optional start date filter (inclusive)
     * @param fechaHasta optional end date filter (inclusive)
     * @return list of orders matching criteria
     */
    List<OrderDTO> getAllOrders(String estado, LocalDate fechaDesde, LocalDate fechaHasta);

    /**
     * Retrieves a specific order by ID.
     * 
     * @param ordenCompraId the order ID
     * @return the order DTO
     * @throws EntityNotFoundException if order not found
     */
    OrderDTO getOrderById(Long ordenCompraId);

    /**
     * Creates a new order with BORRADOR state.
     * Validates provider exists and calculates total from details.
     * 
     * @param orderDTO the order data
     * @return the created order with generated ID and reference code
     * @throws ConflictException if provider doesn't exist
     * @throws ValidationException if order details are invalid
     */
    OrderDTO createOrder(OrderDTO orderDTO);

    /**
     * Updates an existing order.
     * Only orders in BORRADOR state can be modified.
     * 
     * @param ordenCompraId the order ID to update
     * @param orderDTO the updated order data
     * @return the updated order
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is not in BORRADOR state (422 Unprocessable Entity)
     * @throws ValidationException if order details are invalid
     */
    OrderDTO updateOrder(Long ordenCompraId, OrderDTO orderDTO);

    /**
     * Deletes an order.
     * Only orders in BORRADOR state can be deleted.
     * 
     * @param ordenCompraId the order ID to delete
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is not in BORRADOR state (422 Unprocessable Entity)
     */
    void deleteOrder(Long ordenCompraId);

    /**
     * Transitions an order to a new state following the state machine:
     * - BORRADOR → ENVIADA (send order to provider)
     * - BORRADOR → ANULADA (cancel draft order)
     * - ENVIADA → RECIBIDA (receive order and create lots)
     * - ENVIADA → ANULADA (cancel sent order)
     * - RECIBIDA/ANULADA → No transitions (final states)
     * 
     * @param ordenCompraId the order ID
     * @param newState the target state
     * @return the updated order with new state
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if state transition is invalid (422 Unprocessable Entity)
     */
    OrderDTO transitionOrder(Long ordenCompraId, String newState);

    /**
     * Transitions an order from BORRADOR to ENVIADA state.
     * Validates that order is in BORRADOR state and has valid details.
     * 
     * @param id the order ID
     * @return the updated order with ENVIADA state
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is not in BORRADOR state
     */
    OrderDTO transitionToEnviada(Long id);

    /**
     * Transitions an order from ENVIADA to RECIBIDA state.
     * Creates inventory lots automatically based on order details.
     * 
     * @param id the order ID
     * @return the updated order with RECIBIDA state
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is not in ENVIADA state
     */
    OrderDTO transitionToRecibida(Long id);

    /**
     * Transitions an order to ANULADA state.
     * Can be done from BORRADOR or ENVIADA states only.
     * 
     * @param id the order ID
     * @return the updated order with ANULADA state
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is in RECIBIDA state (cannot cancel received orders)
     */
    OrderDTO transitionToAnulada(Long id);

    /**
     * Calculates order total from details.
     * Sum of (cantidad × precioUnitario) for all details.
     * 
     * @param orderDTO the order with details
     * @return the calculated total amount
     */
    OrderDTO calculateOrderTotal(OrderDTO orderDTO);

    /**
     * Calculates order total from detail list.
     * Sum of (cantidad × precioUnitario) for all details.
     * 
     * @param details the order details list
     * @return the calculated total amount
     */
    java.math.BigDecimal calculateOrderTotal(List<OrderDetailDTO> details);

    /**
     * Adds a detail to an existing order.
     * Only allowed if order is in BORRADOR state.
     * 
     * @param orderId the order ID
     * @param detail the detail to add
     * @return the updated order with new detail
     * @throws EntityNotFoundException if order not found
     * @throws BusinessRuleException if order is not in BORRADOR state
     */
    OrderDTO addOrderDetail(Long orderId, OrderDetailDTO detail);

    /**
     * Removes a detail from an existing order.
     * Only allowed if order is in BORRADOR state.
     * 
     * @param orderId the order ID
     * @param detailId the detail ID to remove
     * @return the updated order without the detail
     * @throws EntityNotFoundException if order or detail not found
     * @throws BusinessRuleException if order is not in BORRADOR state
     */
    OrderDTO removeOrderDetail(Long orderId, Long detailId);

    /**
     * Validates if an order can transition to the given state.
     * 
     * @param currentState the current order state
     * @param newState the target state
     * @return true if transition is valid
     */
    boolean canTransitionTo(String currentState, String newState);

    /**
     * Retrieves orders by provider ID with pagination.
     * 
     * @param proveedorId the provider ID
     * @param page the page number (0-based)
     * @param size the page size
     * @return page of orders for the provider
     */
    Page<OrderDTO> getOrdersByProvider(Long proveedorId, int page, int size);

    /**
     * Searches orders by reference code.
     * 
     * @param codigoReferencia the reference code to search
     * @return the order with matching reference code
     * @throws EntityNotFoundException if order not found
     */
    OrderDTO getOrderByReferenceCode(String codigoReferencia);
}