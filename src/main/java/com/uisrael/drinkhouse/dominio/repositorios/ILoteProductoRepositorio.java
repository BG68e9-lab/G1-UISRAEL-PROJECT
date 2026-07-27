package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoRepositorio {

	LoteProducto guardar(LoteProducto loteProducto);

	/**
	 * Guarda el lote asociándolo al producto mediante su ID JPA.
	 */
	LoteProducto guardarConProductoId(LoteProducto loteProducto, Long productoId);

	Optional<LoteProducto> buscarPorId(Long id);

	List<LoteProducto> buscarPorProductoOrdenadoPorFechaIngreso(Long productoId);

	List<LoteProducto> buscarProximosAVencer(LocalDate limite);

	List<LoteProducto> listarTodos();

	/**
	 * Lista todos los lotes con paginación.
	 */
	Page<LoteProducto> listarPaginado(Pageable pageable);

	void eliminar(Long id);
}
