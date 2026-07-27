package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

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
	 * Conveniencia para tests legacy — usa negocioId=1 y busca tipoMovimientoId=1.
	 * No usar en producción.
	 */
	default Long siguiente(String tipo) {
		// Para tests legacy que no tienen contexto de negocio/tipo
		return siguiente(1, 1);
	}
}
