package com.uisrael.drinkhouse.dominio.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IProductoRepositorio {

	Producto guardar(Producto producto);

	Optional<Producto> buscarPorId(Long id);

	List<Producto> listarTodos();

	void eliminar(Long id);

	boolean existePorNombre(String nombre);

	List<Producto> buscarConFiltros(String nombre, String marca, Long tipoProductoId, Long categoriaId);

	
	Producto lockForUpdate(Long productoId);

	
	void actualizarStock(Long productoId, BigDecimal newStock);
}
