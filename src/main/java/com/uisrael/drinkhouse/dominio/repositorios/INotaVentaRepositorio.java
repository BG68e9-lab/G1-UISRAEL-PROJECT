package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;

/**
 * Puerto de salida para operaciones de persistencia de notas de venta.
 */
public interface INotaVentaRepositorio {

	/**
	 * Guarda una nueva nota de venta.
	 * @param notaVenta la nota a guardar
	 * @return la nota guardada con ID generado
	 */
	NotaVenta guardar(NotaVenta notaVenta);

	/**
	 * Lista todas las notas de venta ordenadas por fecha descendente.
	 * @return lista de notas
	 */
	List<NotaVenta> listarTodas();

	/**
	 * Busca una nota por su ID.
	 * @param notaId el ID de la nota
	 * @return Optional con la nota si existe
	 */
	Optional<NotaVenta> buscarPorId(Long notaId);

	/**
	 * Elimina una nota por su ID.
	 * @param notaId el ID de la nota
	 */
	void eliminar(Long notaId);
}
