package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;

/**
 * Puerto de entrada para operaciones de consulta de notas de venta.
 */
public interface INotaVentaUseCase {

	/**
	 * Lista todas las notas de venta ordenadas por fecha de creación descendente.
	 * @return lista de notas de venta
	 */
	List<NotaVenta> listarTodas();

	/**
	 * Busca una nota de venta por su ID.
	 * @param notaId ID de la nota
	 * @return la nota encontrada
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException si no existe
	 */
	NotaVenta buscarPorId(Long notaId);
}
