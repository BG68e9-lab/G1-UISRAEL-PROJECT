package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;

public interface ITipoProductoUseCase {

	TipoProducto crear(TipoProducto tipoProducto);

	TipoProducto actualizar(TipoProducto tipoProducto);

	Optional<TipoProducto> buscarPorId(Long id);

	List<TipoProducto> listarTodos();

	List<TipoProducto> listarPorCategoria(Long categoriaId);

	void desactivar(Long id);

	void eliminar(Long id);

}
