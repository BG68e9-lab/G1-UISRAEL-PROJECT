package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;

public interface ITipoProductoRepositorio {

	TipoProducto guardar(TipoProducto tipoProducto);

	Optional<TipoProducto> buscarPorId(Long id);

	List<TipoProducto> listarTodos();

	List<TipoProducto> listarPorCategoria(Long categoriaId);

	void eliminar(Long id);

	boolean existePorNombreYCategoria(String nombre, Long categoriaId);

	boolean tieneProductosAsociados(Long id);

}
