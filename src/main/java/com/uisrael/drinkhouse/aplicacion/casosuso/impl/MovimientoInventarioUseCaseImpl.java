package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConcurrentModificationException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.aplicacion.servicios.StockValidator;
import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.dominio.entidades.Venta;
import com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.INotaVentaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IVentaRepositorio;

/**
 * Implementación del caso de uso de movimientos de inventario.
 * Gestiona entradas, salidas y ajustes de stock con trazabilidad completa.
 */
public class MovimientoInventarioUseCaseImpl implements IMovimientoInventarioUseCase {

	private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioUseCaseImpl.class);

	private final IMovimientoInventarioRepositorio repositorio;
	private final IProductoRepositorio productoRepositorio;
	private final ILoteProductoRepositorio loteRepositorio;
	private final ITipoMovimientoRepositorio tipoMovimientoRepositorio;
	private final ISecuenciaCodigoUseCase secuenciaUseCase;
	private final IAlertaUseCase alertaUseCase;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;
	private final IAjusteInventarioAuditoriaRepositorio ajusteAuditoriaRepositorio;
	private final StockValidator stockValidator;
	private final ICodigoAccesoUseCase codigoAccesoUseCase;
	private final INotaVentaRepositorio notaVentaRepositorio;
	private final IVentaRepositorio ventaRepositorio;

	public MovimientoInventarioUseCaseImpl(
			IMovimientoInventarioRepositorio repositorio,
			IProductoRepositorio productoRepositorio,
			ILoteProductoRepositorio loteRepositorio,
			ITipoMovimientoRepositorio tipoMovimientoRepositorio,
			ISecuenciaCodigoUseCase secuenciaUseCase,
			IAlertaUseCase alertaUseCase,
			ILogAuditoriaUseCase logAuditoriaUseCase,
			IAjusteInventarioAuditoriaRepositorio ajusteAuditoriaRepositorio,
			StockValidator stockValidator,
			ICodigoAccesoUseCase codigoAccesoUseCase,
			INotaVentaRepositorio notaVentaRepositorio,
			IVentaRepositorio ventaRepositorio) {
		this.repositorio = repositorio;
		this.productoRepositorio = productoRepositorio;
		this.loteRepositorio = loteRepositorio;
		this.tipoMovimientoRepositorio = tipoMovimientoRepositorio;
		this.secuenciaUseCase = secuenciaUseCase;
		this.alertaUseCase = alertaUseCase;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
		this.ajusteAuditoriaRepositorio = ajusteAuditoriaRepositorio;
		this.stockValidator = stockValidator;
		this.codigoAccesoUseCase = codigoAccesoUseCase;
		this.notaVentaRepositorio = notaVentaRepositorio;
		this.ventaRepositorio = ventaRepositorio;
	}

	/**
	 * Creates an inventory movement with secondary authentication and complete audit trail.
	 * Implements retry logic for optimistic locking failures.
	 * 
	 * @param movimiento Movement details including stock validation fields
	 * @param usuarioAutorizado Username from secondary authentication
	 * @param usuarioEjecutor Primary session username
	 * @param justificacion Reason for the movement (10-500 chars)
	 * @param direccionIp Client IP address
	 * @param sessionId Session identifier
	 * @return Created movement with generated ID
	 * @throws StockValidationException if stock calculations are invalid
	 * @throws ReglaNegocioException if secondary auth fails
	 * @throws ConcurrentModificationException if product was modified concurrently
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public MovimientoInventario crearMovimientoConAuditoria(
			MovimientoInventario movimiento,
			String usuarioAutorizado,
			String usuarioEjecutor,
			String justificacion,
			String direccionIp,
			String sessionId) {
		
		logger.info("Iniciando creación de movimiento con auditoría - Usuario ejecutor: {}, Producto ID: {}, Ajuste: {}", 
				usuarioEjecutor, movimiento.getProductoId(), movimiento.getAjuste());
		
		int maxRetries = 3;
		int attempt = 0;
		
		while (attempt < maxRetries) {
			try {
				MovimientoInventario resultado = crearMovimientoConAuditoriaInternal(
					movimiento, 
					usuarioAutorizado, 
					usuarioEjecutor, 
					justificacion, 
					direccionIp, 
					sessionId
				);
				
				logger.info("Movimiento creado exitosamente - ID: {}, Usuario ejecutor: {}, Producto ID: {}, Ajuste: {}", 
						resultado.getMovimientoId(), usuarioEjecutor, resultado.getProductoId(), movimiento.getAjuste());
				
				return resultado;
			} catch (ObjectOptimisticLockingFailureException e) {
				attempt++;
				
				logger.warn("Conflicto de concurrencia detectado en intento {} de {} - Producto ID: {}, Usuario: {}", 
						attempt, maxRetries, movimiento.getProductoId(), usuarioEjecutor);
				
				logAuditoriaUseCase.registrar(
					"MovimientoInventario",
					movimiento.getProductoId().toString(),
					"CONCURRENT_MODIFICATION_DETECTED",
					"Conflicto de concurrencia detectado. Intento " + attempt + " de " + maxRetries
				);
				
				if (attempt >= maxRetries) {
					logger.error("Máximo de reintentos alcanzado para conflicto de concurrencia - Producto ID: {}, Usuario: {}", 
							movimiento.getProductoId(), usuarioEjecutor);
					throw new ConcurrentModificationException(
						"Conflicto de concurrencia: el stock del producto fue modificado por otra operación. Intente nuevamente"
					);
				}
				
				productoRepositorio.buscarPorId(movimiento.getProductoId())
					.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + movimiento.getProductoId()));
			}
		}
		
		throw new ConcurrentModificationException(
			"Conflicto de concurrencia: el stock del producto fue modificado por otra operación. Intente nuevamente"
		);
	}
	
	/**
	 * Internal method that performs the actual movement creation.
	 * Separated to enable retry logic.
	 */
	private MovimientoInventario crearMovimientoConAuditoriaInternal(
			MovimientoInventario movimiento,
			String usuarioAutorizado,
			String usuarioEjecutor,
			String justificacion,
			String direccionIp,
			String sessionId) {
		
		try {
			codigoAccesoUseCase.validarCodigo(usuarioAutorizado);
			logger.info("Autenticación secundaria validada - Usuario autorizado: {}", usuarioAutorizado);
			
		} catch (ReglaNegocioException e) {
			logger.warn("Fallo en autenticación secundaria - Usuario autorizado: {}, Motivo: {}", 
					usuarioAutorizado, e.getMessage());
			throw e;
		}
		
		Producto producto;
		try {
			producto = productoRepositorio.buscarPorId(movimiento.getProductoId())
				.orElseThrow(() -> new RecursoNoEncontradoException(
					"Producto no encontrado con id: " + movimiento.getProductoId()));
			
		} catch (RecursoNoEncontradoException e) {
			logger.warn("Validación fallida - Producto no encontrado: {}", movimiento.getProductoId());
			throw e;
		}
		
		if (movimiento.getLoteId() != null) {
			try {
				loteRepositorio.buscarPorId(movimiento.getLoteId())
					.orElseThrow(() -> new RecursoNoEncontradoException(
						"Lote no encontrado con id: " + movimiento.getLoteId()));
				
			} catch (RecursoNoEncontradoException e) {
				logger.warn("Validación fallida - Lote no encontrado: {}", movimiento.getLoteId());
				throw e;
			}
		}
		
		producto = productoRepositorio.lockForUpdate(movimiento.getProductoId());
		
		BigDecimal currentStock = BigDecimal.valueOf(producto.getStockActual());
		try {
			stockValidator.validateCurrentStock(
				producto.getProductoId(),
				movimiento.getCantidadAnterior(),
				currentStock
			);
		} catch (Exception e) {
			logger.warn("Validación de stock fallida - Producto ID: {}, Cantidad anterior esperada: {}, Stock actual: {}", 
					producto.getProductoId(), movimiento.getCantidadAnterior(), currentStock);
			throw e;
		}
		
		try {
			stockValidator.validateStockCalculation(
				movimiento.getCantidadAnterior(),
				movimiento.getAjuste(),
				movimiento.getCantidadPosterior()
			);
		} catch (Exception e) {
			logger.warn("Validación de cálculo de stock fallida - Cantidad anterior: {}, Ajuste: {}, Cantidad posterior: {}", 
					movimiento.getCantidadAnterior(), movimiento.getAjuste(), movimiento.getCantidadPosterior());
			throw e;
		}
		
		try {
			stockValidator.validateNegativeStock(
				producto.getProductoId(),
				movimiento.getCantidadPosterior(),
				producto.getPermiteStockNegativo()
			);
		} catch (Exception e) {
			logger.warn("Validación de stock negativo fallida - Producto ID: {}, Cantidad posterior: {}, Permite stock negativo: {}", 
					producto.getProductoId(), movimiento.getCantidadPosterior(), producto.getPermiteStockNegativo());
			throw e;
		}
		
		TipoMovimiento tipoMovimiento;
		try {
			tipoMovimiento = tipoMovimientoRepositorio.buscarPorId(
					movimiento.getTipoMovimientoId().intValue())
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"Tipo de movimiento no encontrado con id: " + movimiento.getTipoMovimientoId()));
		} catch (RecursoNoEncontradoException e) {
			logger.warn("Validación fallida - Tipo de movimiento no encontrado: {}", movimiento.getTipoMovimientoId());
			throw e;
		}
		
		String codigoTipo = tipoMovimiento.getCodigo();
		if (!isValidTipoMovimiento(codigoTipo)) {
			logger.warn("Validación fallida - Tipo de movimiento inválido: {}", codigoTipo);
			throw new ReglaNegocioException(
				"Tipo de movimiento inválido: " + codigoTipo + 
				". Debe ser uno de: ENTRADA, SALIDA, AJUSTE_POSITIVO, AJUSTE_NEGATIVO");
		}
		
		Long secuencia = secuenciaUseCase.siguiente(
			producto.getNegocioId() != null ? producto.getNegocioId() : 1,
			tipoMovimiento.getTipoMovimientoId());
		String codigoMovimiento = tipoMovimiento.getPrefijoCodigo()
			+ String.format("%08d", secuencia);
		
		movimiento.setCodigoMovimiento(codigoMovimiento);
		movimiento.setTipoMovimientoCodigo(tipoMovimiento.getCodigo());
		movimiento.setCreadoEn(OffsetDateTime.now());
		
		MovimientoInventario movimientoGuardado = repositorio.guardar(movimiento);
		
		if (movimiento.getLoteId() != null) {
			LoteProducto lote = loteRepositorio.buscarPorId(movimiento.getLoteId())
				.orElseThrow(() -> new RecursoNoEncontradoException(
					"Lote no encontrado con id: " + movimiento.getLoteId()));
			
			BigDecimal newLoteStock = lote.getCantidadDisponible().add(movimiento.getAjuste());
			lote.setCantidadDisponible(newLoteStock);
			loteRepositorio.guardar(lote);
			
			BigDecimal newStock = movimiento.getCantidadPosterior();
			productoRepositorio.actualizarStock(producto.getProductoId(), newStock);
		} else {
			BigDecimal newStock = movimiento.getCantidadPosterior();
			productoRepositorio.actualizarStock(producto.getProductoId(), newStock);
		}
		
		AjusteInventarioAuditoria auditoria = new AjusteInventarioAuditoria();
		auditoria.setMovimientoId(movimientoGuardado.getMovimientoId());
		auditoria.setProductoId(movimiento.getProductoId());
		auditoria.setLoteId(movimiento.getLoteId());
		auditoria.setTipoMovimiento(tipoMovimiento.getCodigo());
		auditoria.setCantidadAnterior(movimiento.getCantidadAnterior());
		auditoria.setAjuste(movimiento.getAjuste());
		auditoria.setCantidadPosterior(movimiento.getCantidadPosterior());
		auditoria.setUsuarioAutorizado(usuarioAutorizado);
		auditoria.setUsuarioEjecutor(usuarioEjecutor);
		auditoria.setJustificacion(justificacion);
		auditoria.setFechaHora(OffsetDateTime.now());
		auditoria.setDireccionIp(direccionIp);
		auditoria.setSessionId(sessionId);
		auditoria.setVentaId(movimiento.getVentaId());
		
		ajusteAuditoriaRepositorio.guardar(auditoria);
		
		logAuditoriaUseCase.registrar(
			"MovimientoInventario",
			movimientoGuardado.getMovimientoId().toString(),
			"CREAR_CON_AUDITORIA",
			movimientoGuardado
		);
		
		return movimientoGuardado;
	}

	/**
	 * Registra un movimiento de inventario ejecutando la lógica de negocio
	 * correspondiente al tipo (ENTRADA, SALIDA, AJUSTE) dentro de una transacción.
	 */
	@Override
	@Transactional
	public MovimientoInventario registrar(Long productoId, Long loteId,
			Long tipoMovimientoId, MovimientoInventario movimiento) {

		Producto producto = productoRepositorio.buscarPorId(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + productoId));

		TipoMovimiento tipoMovimiento = tipoMovimientoRepositorio.buscarPorId(
				tipoMovimientoId.intValue())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de movimiento no encontrado con id: " + tipoMovimientoId));

		BigDecimal cantidad = movimiento.getCantidad();
		String codigo = tipoMovimiento.getCodigo();

		if (("ENTRADA".equalsIgnoreCase(codigo) || "SALIDA".equalsIgnoreCase(codigo))
				&& cantidad.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaNegocioException(
					"La cantidad para movimientos de tipo " + codigo + " debe ser positiva");
		}

		if ("AJUSTE".equalsIgnoreCase(codigo) && cantidad.compareTo(BigDecimal.ZERO) == 0) {
			throw new ReglaNegocioException(
					"La cantidad para ajustes no puede ser cero");
		}

		BigDecimal nuevoStock;
		if ("ENTRADA".equalsIgnoreCase(codigo)) {
			nuevoStock = BigDecimal.valueOf(producto.getStockActual()).add(cantidad);
		} else if ("SALIDA".equalsIgnoreCase(codigo)) {
			LoteProducto lote = cargarLote(loteId);
			if (lote.getCantidadDisponible().compareTo(cantidad) < 0) {
				throw new ReglaNegocioException(
						"Cantidad insuficiente en el lote. Disponible: "
								+ lote.getCantidadDisponible() + ", solicitado: " + cantidad);
			}
			lote.setCantidadDisponible(lote.getCantidadDisponible().subtract(cantidad));
			loteRepositorio.guardar(lote);
			loteId = lote.getLoteId();
			nuevoStock = BigDecimal.valueOf(producto.getStockActual()).subtract(cantidad);
		} else if ("VENTA".equalsIgnoreCase(codigo)) {
			nuevoStock = BigDecimal.valueOf(producto.getStockActual()).subtract(cantidad);
		} else {
			nuevoStock = BigDecimal.valueOf(producto.getStockActual()).add(cantidad);
		}

		Long secuencia = secuenciaUseCase.siguiente(
				producto.getNegocioId() != null ? producto.getNegocioId() : 1,
				tipoMovimiento.getTipoMovimientoId());
		String codigoMovimiento = tipoMovimiento.getPrefijoCodigo()
				+ String.format("%08d", secuencia);

		movimiento.setCodigoMovimiento(codigoMovimiento);
		movimiento.setProductoId(productoId);
		movimiento.setLoteId(loteId);
		movimiento.setTipoMovimientoId(tipoMovimientoId);
		movimiento.setTipoMovimientoCodigo(codigo);
		movimiento.setCreadoEn(OffsetDateTime.now());

		productoRepositorio.actualizarStock(productoId, nuevoStock);

		if (producto.getStockMinimo() != null
				&& nuevoStock.intValue() <= producto.getStockMinimo()) {
			alertaUseCase.crearAlertaStockBajo(producto);
		}

		MovimientoInventario guardado = repositorio.guardar(movimiento);

		logAuditoriaUseCase.registrar(
				"MovimientoInventario",
				guardado.getMovimientoId().toString(),
				"CREAR",
				guardado);

		if ("VENTA".equalsIgnoreCase(codigo)) {
			crearNotaVenta(guardado, producto);
		}

		return guardado;
	}

	/**
	 * Crea automáticamente una nota de venta cuando el movimiento es de tipo VENTA.
	 */
	private void crearNotaVenta(MovimientoInventario movimiento, Producto producto) {
		try {
			NotaVenta nota = new NotaVenta();
			
			nota.setFecha(movimiento.getCreadoEn() != null 
					? movimiento.getCreadoEn().toString().substring(0, 16)
					: OffsetDateTime.now().toString().substring(0, 16));
			
			String nombreCliente = "Cliente anónimo";
			String total = null;
			
			if (movimiento.getVentaId() != null) {
				try {
					Venta venta = ventaRepositorio.buscarPorId(movimiento.getVentaId())
							.orElse(null);
					if (venta != null) {
						nombreCliente = venta.getNombreCliente() != null 
								? venta.getNombreCliente() 
								: "Cliente anónimo";
						total = "$" + venta.getTotal().toString();
					}
				} catch (Exception e) {
					logger.warn("No se pudo obtener datos de venta {}: {}", 
							movimiento.getVentaId(), e.getMessage());
				}
			}
			
			if (total == null && movimiento.getCantidad() != null && movimiento.getPrecioUnitario() != null) {
				BigDecimal totalCalculado = movimiento.getCantidad()
						.abs()
						.multiply(movimiento.getPrecioUnitario());
				total = "$" + totalCalculado.toString();
			}
			
			nota.setNombreCliente(nombreCliente);
			nota.setProductoVendido(producto.getNombre() + " x" + movimiento.getCantidad().abs());
			
			if (movimiento.getPrecioUnitario() != null) {
				nota.setPrecioUnitario("$" + movimiento.getPrecioUnitario().toString());
			}
			
			nota.setTotal(total);
			
			if (movimiento.getVentaId() != null) {
				try {
					Venta venta = ventaRepositorio.buscarPorId(movimiento.getVentaId())
							.orElse(null);
					if (venta != null && venta.getObservaciones() != null) {
						nota.setObservaciones(venta.getObservaciones());
					}
				} catch (Exception e) {
					logger.warn("No se pudieron obtener observaciones de venta {}: {}", 
							movimiento.getVentaId(), e.getMessage());
				}
			}
			
			notaVentaRepositorio.guardar(nota);
			logger.info("Nota de venta creada automáticamente para movimiento {}", 
					movimiento.getMovimientoId());
		} catch (Exception e) {
			logger.error("Error al crear nota de venta para movimiento {}: {}", 
					movimiento.getMovimientoId(), e.getMessage());
		}
	}

	/**
	 * Consulta movimientos de un producto aplicando filtros opcionales.
	 */
	@Override
	public List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId,
			String tipo, OffsetDateTime desde, OffsetDateTime hasta) {
		return repositorio.buscarPorProductoConFiltros(productoId, tipo, desde, hasta);
	}

	/**
	 * Lista todos los movimientos del sistema.
	 */
	@Override
	public List<MovimientoInventario> listarTodos() {
		return repositorio.listarTodos();
	}

	/**
	 * Busca movimientos por tipo de movimiento.
	 */
	@Override
	public List<MovimientoInventario> buscarPorTipo(String codigoTipo) {
		return repositorio.buscarPorTipo(codigoTipo);
	}

	/**
	 * Busca movimientos por lote.
	 */
	@Override
	public List<MovimientoInventario> buscarPorLote(Long loteId) {
		return repositorio.buscarPorLote(loteId);
	}

	/**
	 * Busca un movimiento por su ID.
	 */
	@Override
	public MovimientoInventario buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Movimiento de inventario no encontrado con id: " + id));
	}

	/** ENTRADA: incrementa el stock actual del producto. */
	private void aplicarEntrada(Producto producto, BigDecimal cantidad) {
		int nuevoStock = producto.getStockActual() + cantidad.intValue();
		producto.setStockActual(nuevoStock);
	}

	/**
	 * SALIDA: verifica disponibilidad en el lote y decrementa tanto el lote
	 * como el stock del producto.
	 */
	private void aplicarSalida(Producto producto, LoteProducto lote, BigDecimal cantidad) {
		if (lote.getCantidadDisponible().compareTo(cantidad) < 0) {
			throw new ReglaNegocioException(
					"Cantidad insuficiente en el lote. Disponible: "
							+ lote.getCantidadDisponible() + ", solicitado: " + cantidad);
		}
		lote.setCantidadDisponible(lote.getCantidadDisponible().subtract(cantidad));
		loteRepositorio.guardar(lote);

		int nuevoStock = producto.getStockActual() - cantidad.intValue();
		producto.setStockActual(nuevoStock);
	}

	/**
	 * AJUSTE: actualiza el stock del producto sumando la cantidad
	 * (puede ser negativa para disminución).
	 */
	private void aplicarAjuste(Producto producto, BigDecimal cantidad) {
		int nuevoStock = producto.getStockActual() + cantidad.intValue();
		producto.setStockActual(nuevoStock);
	}

	/** Carga el lote o lanza excepción 404 si no existe. */
	private LoteProducto cargarLote(Long loteId) {
		if (loteId == null) {
			throw new ReglaNegocioException(
					"El loteId es requerido para movimientos de tipo SALIDA");
		}
		return loteRepositorio.buscarPorId(loteId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Lote no encontrado con id: " + loteId));
	}
	
	/**
	 * Validates that tipo_movimiento is one of the allowed enumeration values.
	 * @param codigo Tipo movimiento code to validate
	 * @return true if valid, false otherwise
	 */
	private boolean isValidTipoMovimiento(String codigo) {
		if (codigo == null) {
			return false;
		}
		return codigo.equals("ENTRADA") || 
		       codigo.equals("SALIDA") || 
		       codigo.equals("AJUSTE_POSITIVO") || 
		       codigo.equals("AJUSTE_NEGATIVO");
	}
}
