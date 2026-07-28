package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoRepositorio {

	LoteProducto guardar(LoteProducto loteProducto);

	Optional<LoteProducto> buscarPorId(Long id);

	List<LoteProducto> listarTodos();

	List<LoteProducto> listarPorProducto(Long productoId);

	List<LoteProducto> listarProximosAVencer(int dias);

	LoteProducto actualizarCantidad(Long id, Integer nuevaCantidadDisponible);

	LoteProducto activar(Long id);

	LoteProducto desactivar(Long id);

	void eliminar(Long id);

}
