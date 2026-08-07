package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoRepositorio {

	LoteProducto guardar(LoteProducto loteProducto);

LoteProducto guardarConProductoId(LoteProducto loteProducto, Long productoId);

	Optional<LoteProducto> buscarPorId(Long id);

	List<LoteProducto> buscarPorProductoOrdenadoPorFechaIngreso(Long productoId);

	List<LoteProducto> buscarProximosAVencer(LocalDate limite);

	List<LoteProducto> listarTodos();

Page<LoteProducto> listarPaginado(Pageable pageable);

	void eliminar(Long id);
}
