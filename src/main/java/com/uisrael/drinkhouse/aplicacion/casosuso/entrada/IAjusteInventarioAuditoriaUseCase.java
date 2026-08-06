package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;

/**
 * Puerto de entrada para el módulo de auditoría de ajustes de inventario.
 * Proporciona acceso a los registros de auditoría de movimientos de inventario.
 */
public interface IAjusteInventarioAuditoriaUseCase {

	/**
	 * Busca el registro de auditoría asociado a un movimiento de inventario.
	 *
	 * @param movimientoId ID del movimiento de inventario
	 * @return el registro de auditoría completo
	 * @throws com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException si no se encuentra el registro
	 */
	AjusteInventarioAuditoria buscarPorMovimiento(Long movimientoId);

}
