package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;

public interface ISecuenciaCodigoUseCase {

	/**
	 * Genera el siguiente número de secuencia para un negocio y tipo de movimiento.
	 *
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @return número siguiente de forma atómica
	 */
	Long siguiente(Integer negocioId, Integer tipoMovimientoId);

	/**
	 * Lista todas las secuencias configuradas.
	 *
	 * @return lista de secuencias
	 */
	List<SecuenciaCodigo> listarTodas();

	/**
	 * Lista las secuencias de un negocio específico.
	 *
	 * @param negocioId ID del negocio
	 * @return lista de secuencias del negocio
	 */
	List<SecuenciaCodigo> listarPorNegocio(Integer negocioId);

	/**
	 * Busca una secuencia específica.
	 *
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @return secuencia encontrada
	 */
	SecuenciaCodigo buscar(Integer negocioId, Integer tipoMovimientoId);

	/**
	 * Crea o inicializa una nueva secuencia.
	 *
	 * @param secuencia datos de la secuencia
	 * @return secuencia creada
	 */
	SecuenciaCodigo crear(SecuenciaCodigo secuencia);

	/**
	 * Actualiza el último número de una secuencia existente.
	 *
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param nuevoNumero      nuevo último número
	 * @return secuencia actualizada
	 */
	SecuenciaCodigo actualizar(Integer negocioId, Integer tipoMovimientoId, Long nuevoNumero);

	/**
	 * Elimina una secuencia.
	 *
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 */
	void eliminar(Integer negocioId, Integer tipoMovimientoId);

	/**
	 * Reinicia una secuencia a un valor específico.
	 *
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param valorInicial     valor al que reiniciar
	 * @return secuencia reiniciada
	 */
	SecuenciaCodigo reiniciar(Integer negocioId, Integer tipoMovimientoId, Long valorInicial);

	/**
	 * Inicializa todas las secuencias necesarias (negocios × tipos de movimiento).
	 * Crea las secuencias faltantes automáticamente.
	 *
	 * @return número de secuencias creadas
	 */
	int inicializarSecuenciasParaTodosLosNegocios();

	/**
	 * Conveniencia para tests legacy — usa negocioId=1 y busca tipoMovimientoId=1.
	 * No usar en producción.
	 */
	default Long siguiente(String tipo) {
		return siguiente(1, 1);
	}
}
