package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAlertaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

/**
 * Implementación del caso de uso de movimientos de inventario.
 * Gestiona entradas, salidas y ajustes de stock con trazabilidad completa.
 */
public class MovimientoInventarioUseCaseImpl implements IMovimientoInventarioUseCase {

	private final IMovimientoInventarioRepositorio repositorio;
	private final IProductoRepositorio productoRepositorio;
	private final ILoteProductoRepositorio loteRepositorio;
	private final ITipoMovimientoRepositorio tipoMovimientoRepositorio;
	private final ISecuenciaCodigoUseCase secuenciaUseCase;
	private final IAlertaUseCase alertaUseCase;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public MovimientoInventarioUseCaseImpl(
			IMovimientoInventarioRepositorio repositorio,
			IProductoRepositorio productoRepositorio,
			ILoteProductoRepositorio loteRepositorio,
			ITipoMovimientoRepositorio tipoMovimientoRepositorio,
			ISecuenciaCodigoUseCase secuenciaUseCase,
			IAlertaUseCase alertaUseCase,
			ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.productoRepositorio = productoRepositorio;
		this.loteRepositorio = loteRepositorio;
		this.tipoMovimientoRepositorio = tipoMovimientoRepositorio;
		this.secuenciaUseCase = secuenciaUseCase;
		this.alertaUseCase = alertaUseCase;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	/**
	 * Registra un movimiento de inventario ejecutando la lógica de negocio
	 * correspondiente al tipo (ENTRADA, SALIDA, AJUSTE) dentro de una transacción.
	 */
	@Override
	@Transactional
	public MovimientoInventario registrar(Long productoId, Long loteId,
			Long tipoMovimientoId, MovimientoInventario movimiento) {

		// Cargar entidades necesarias
		Producto producto = productoRepositorio.buscarPorId(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Producto no encontrado con id: " + productoId));

		TipoMovimiento tipoMovimiento = tipoMovimientoRepositorio.buscarPorId(
				tipoMovimientoId.intValue())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de movimiento no encontrado con id: " + tipoMovimientoId));

		BigDecimal cantidad = movimiento.getCantidad();
		String codigo = tipoMovimiento.getCodigo();

		// Aplicar lógica según tipo de movimiento
		if ("ENTRADA".equalsIgnoreCase(codigo)) {
			aplicarEntrada(producto, cantidad);
		} else if ("SALIDA".equalsIgnoreCase(codigo)) {
			LoteProducto lote = cargarLote(loteId);
			aplicarSalida(producto, lote, cantidad);
			loteId = lote.getLoteId();
		} else {
			// AJUSTE: cantidad puede ser positiva o negativa
			aplicarAjuste(producto, cantidad);
		}

		// Generar código único de movimiento
		Long secuencia = secuenciaUseCase.siguiente(
				producto.getNegocioId() != null ? producto.getNegocioId() : 1,
				tipoMovimiento.getTipoMovimientoId());
		String codigoMovimiento = tipoMovimiento.getPrefijoCodigo()
				+ String.format("%08d", secuencia);

		// Persistir el movimiento
		movimiento.setCodigoMovimiento(codigoMovimiento);
		movimiento.setProductoId(productoId);
		movimiento.setLoteId(loteId);
		movimiento.setTipoMovimientoId(tipoMovimientoId);
		movimiento.setTipoMovimientoCodigo(codigo);
		movimiento.setCreadoEn(OffsetDateTime.now());

		// Guardar el producto actualizado
		productoRepositorio.guardar(producto);

		// Verificar alerta de stock bajo post-movimiento
		if (producto.getStockActual() != null && producto.getStockMinimo() != null
				&& producto.getStockActual() <= producto.getStockMinimo()) {
			alertaUseCase.crearAlertaStockBajo(producto);
		}

		// Persistir el movimiento
		MovimientoInventario guardado = repositorio.guardar(movimiento);

		// Registrar en auditoría
		logAuditoriaUseCase.registrar(
				"MovimientoInventario",
				guardado.getMovimientoId().toString(),
				"CREAR",
				guardado);

		return guardado;
	}

	/**
	 * Consulta movimientos de un producto aplicando filtros opcionales.
	 */
	@Override
	public List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId,
			String tipo, OffsetDateTime desde, OffsetDateTime hasta) {
		return repositorio.buscarPorProductoConFiltros(productoId, tipo, desde, hasta);
	}

	// --- Métodos privados de lógica de negocio ---

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
}
