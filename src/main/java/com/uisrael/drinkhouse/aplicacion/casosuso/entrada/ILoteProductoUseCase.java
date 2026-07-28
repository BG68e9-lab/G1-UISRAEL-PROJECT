package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoUseCase {

	LoteProducto crear(LoteProducto loteProducto);

	LoteProducto actualizar(Long id, LoteProducto loteProducto);

	LoteProducto buscarPorId(Long id);

	List<LoteProducto> listar(Long productoId);

	List<LoteProducto> listarProximosAVencer(int dias);

	LoteProducto actualizarCantidad(Long id, Integer cantidadDisponible);

	LoteProducto activar(Long id);

	LoteProducto desactivar(Long id);

	void eliminar(Long id);
}
