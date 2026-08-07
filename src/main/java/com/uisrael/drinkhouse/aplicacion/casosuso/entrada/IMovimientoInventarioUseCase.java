package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;

public interface IMovimientoInventarioUseCase {

	MovimientoInventario registrar(Long productoId, Long loteId, Long tipoMovimientoId,
			MovimientoInventario movimiento);

	List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta);

	List<MovimientoInventario> listarTodos();

	List<MovimientoInventario> buscarPorTipo(String codigoTipo);

	List<MovimientoInventario> buscarPorLote(Long loteId);

	MovimientoInventario buscarPorId(Long id);

	MovimientoInventario crearMovimientoConAuditoria(
			MovimientoInventario movimiento,
			String usuarioAutorizado,
			String usuarioEjecutor,
			String justificacion,
			String direccionIp,
			String sessionId);
}
