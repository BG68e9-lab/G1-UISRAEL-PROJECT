package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;

/**
 * Puerto de entrada para el módulo de Tipos de Producto.
 * Define las operaciones de negocio relacionadas con los tipos de producto
 * que pertenecen a categorías específicas.
 */
public interface ITipoProductoUseCase {

	/**
	 * Crea un nuevo tipo de producto asociado a una categoría.
	 * @param tipoProducto El tipo de producto a crear
	 * @return El tipo de producto creado con su ID asignado
	 */
	TipoProducto crear(TipoProducto tipoProducto);

	/**
	 * Actualiza un tipo de producto existente.
	 * @param tipoProducto El tipo de producto con los datos actualizados
	 * @return El tipo de producto actualizado
	 */
	TipoProducto actualizar(TipoProducto tipoProducto);

	/**
	 * Busca un tipo de producto por su ID.
	 * @param id El ID del tipo de producto
	 * @return Un Optional con el tipo de producto si existe
	 */
	Optional<TipoProducto> buscarPorId(Long id);

	/**
	 * Lista todos los tipos de producto activos.
	 * @return Lista de todos los tipos de producto
	 */
	List<TipoProducto> listarTodos();

	/**
	 * Lista todos los tipos de producto de una categoría específica.
	 * @param categoriaId El ID de la categoría
	 * @return Lista de tipos de producto de la categoría
	 */
	List<TipoProducto> listarPorCategoria(Long categoriaId);

	/**
	 * Desactiva un tipo de producto (borrado lógico).
	 * @param id El ID del tipo de producto a desactivar
	 */
	void desactivar(Long id);

	/**
	 * Elimina un tipo de producto si no tiene productos asociados.
	 * @param id El ID del tipo de producto a eliminar
	 */
	void eliminar(Long id);

}
