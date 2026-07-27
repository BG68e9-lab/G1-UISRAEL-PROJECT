package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

/**
 * Puerto de entrada para el módulo de Lotes de Producto.
 * Cubre la creación y consulta de lotes con trazabilidad FIFO.
 */
public interface ILoteProductoUseCase {

	/**
	 * Crea un nuevo lote de producto.
	 * Asigna cantidadDisponible = cantidadInicial, fechaIngreso = ahora
	 * y genera el codigoEntrada con SecuenciaCodigo.
	 *
	 * @param lote      datos del lote (cantidadInicial, precioCosto, fechaVencimiento)
	 * @param productoId ID del producto al que pertenece el lote
	 * @return lote persistido con loteId y codigoEntrada asignados
	 */
	LoteProducto crearLote(LoteProducto lote, Long productoId);

	/**
	 * Busca todos los lotes de un producto ordenados por fechaIngreso ascendente (FIFO).
	 *
	 * @param productoId ID del producto
	 * @return lista de lotes ordenada por fechaIngreso ASC
	 */
	List<LoteProducto> buscarPorProducto(Long productoId);

	/**
	 * Busca un lote por su identificador.
	 *
	 * @param id loteId a buscar
	 * @return lote encontrado
	 * @throws com.uisrael.drinkhouse.dominio.excepciones.RecursoNoEncontradoException si no existe
	 */
	LoteProducto buscarPorId(Long id);

	/**
	 * Lista todos los lotes con paginación.
	 *
	 * @param pageable configuración de paginación (page, size, sort)
	 * @return página de lotes
	 */
	Page<LoteProducto> listarPaginado(Pageable pageable);

	/**
	 * Busca los lotes próximos a vencer en los siguientes N días
	 * cuya cantidadDisponible sea mayor a cero.
	 *
	 * @param dias número de días desde hoy para calcular el límite
	 * @return lista de lotes próximos a vencer con stock disponible
	 */
	List<LoteProducto> buscarProximosAVencer(int dias);
}
