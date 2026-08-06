package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

/**
 * Puerto de entrada para el módulo de movimientos de inventario.
 * Cubre registro de entradas, salidas y ajustes de stock.
 */
public interface IMovimientoInventarioUseCase {

	/**
	 * Registra un movimiento de inventario (ENTRADA, SALIDA o AJUSTE).
	 * Aplica la lógica de negocio correspondiente al tipo de movimiento.
	 *
	 * @param productoId       ID del producto afectado
	 * @param loteId           ID del lote (requerido para SALIDA)
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param movimiento       datos del movimiento (cantidad, precioUnitario)
	 * @return el movimiento persistido con codigoMovimiento generado
	 */
	MovimientoInventario registrar(Long productoId, Long loteId, Long tipoMovimientoId,
			MovimientoInventario movimiento);

	/**
	 * Consulta movimientos de un producto con filtros opcionales.
	 *
	 * @param productoId ID del producto
	 * @param tipo       código del tipo de movimiento (opcional)
	 * @param desde      fecha/hora de inicio del rango (opcional)
	 * @param hasta      fecha/hora de fin del rango (opcional)
	 * @return lista de movimientos ordenada por creadoEn descendente
	 */
	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);

	/**
	 * Lista todos los movimientos del sistema.
	 *
	 * @return lista de todos los movimientos ordenada por creadoEn descendente
	 */
	List<MovimientoInventario> listarTodos();

	/**
	 * Busca movimientos por tipo de movimiento.
	 *
	 * @param codigoTipo código del tipo (ENTRADA, SALIDA, AJUSTE)
	 * @return lista de movimientos del tipo especificado
	 */
	List<MovimientoInventario> buscarPorTipo(String codigoTipo);

	/**
	 * Busca movimientos por lote.
	 *
	 * @param loteId ID del lote
	 * @return lista de movimientos del lote
	 */
	List<MovimientoInventario> buscarPorLote(Long loteId);

	/**
	 * Busca un movimiento por su ID.
	 *
	 * @param id ID del movimiento
	 * @return el movimiento encontrado
	 */
	MovimientoInventario buscarPorId(Long id);

	/**
	 * Creates an inventory movement with secondary authentication and complete audit trail.
	 * 
	 * <p>This method orchestrates a transactional operation that includes:
	 * <ul>
	 *   <li>Secondary authentication validation</li>
	 *   <li>Stock quantity validation (cantidad_anterior + ajuste = cantidad_posterior)</li>
	 *   <li>Movement record creation</li>
	 *   <li>Product stock update</li>
	 *   <li>Audit record creation with authorization details</li>
	 * </ul>
	 * 
	 * <p>All operations execute within a single database transaction with READ_COMMITTED 
	 * isolation level. If any step fails, the entire transaction is rolled back to ensure 
	 * data consistency.
	 * 
	 * <p><b>Requirements Validated:</b>
	 * <ul>
	 *   <li><b>Requirement 1.1:</b> POST endpoint acceptance</li>
	 *   <li><b>Requirement 2.1:</b> Transactional processing</li>
	 * </ul>
	 * 
	 * @param movimiento Movement details including stock validation fields (cantidadAnterior, 
	 *                   ajuste, cantidadPosterior), product ID, lote ID (optional), 
	 *                   tipo_movimiento, and venta_id (optional for sales-linked movements)
	 * @param usuarioAutorizado Username obtained from secondary authentication validation. 
	 *                          This represents the user who provided secondary credentials 
	 *                          to authorize this sensitive operation
	 * @param usuarioEjecutor Primary session username. This represents the user who is 
	 *                        executing the operation from their main authenticated session
	 * @param justificacion Business justification for the inventory movement. Must be 
	 *                      between 10 and 500 characters after trimming whitespace. 
	 *                      This text is stored in the audit record for compliance tracking
	 * @param direccionIp Client IP address from which the request originated. Used for 
	 *                    audit trail and security monitoring. Must be valid IPv4 or IPv6 format
	 * @param sessionId Session identifier for the executing user's session. Used to correlate 
	 *                  audit records with user sessions for security analysis
	 * 
	 * @return Created movement with generated ID and all fields populated. The returned 
	 *         entity represents the persisted state after successful transaction commit
	 * 
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.StockValidationException 
	 *         if stock calculations are invalid (cantidad_anterior + ajuste != cantidad_posterior) 
	 *         or if the provided cantidad_anterior does not match the current product stock 
	 *         in the database
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.SecondaryAuthException 
	 *         if secondary authentication validation fails (invalid code, expired code, 
	 *         or already used code)
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.ConcurrentModificationException 
	 *         if the product was modified by another transaction concurrently and retry 
	 *         attempts (max 3) were exhausted
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException 
	 *         if the referenced product, lote, or venta does not exist in the database
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException 
	 *         if business rules are violated (e.g., negative stock not allowed for product, 
	 *         lote required but not provided, etc.)
	 * 
	 * @since 1.0
	 */
	MovimientoInventario crearMovimientoConAuditoria(
			MovimientoInventario movimiento,
			String usuarioAutorizado,
			String usuarioEjecutor,
			String justificacion,
			String direccionIp,
			String sessionId);
}
