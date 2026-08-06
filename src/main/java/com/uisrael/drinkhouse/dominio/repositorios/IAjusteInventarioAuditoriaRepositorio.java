package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;

/**
 * Interfaz de repositorio para la entidad de dominio AjusteInventarioAuditoria.
 * Define operaciones para guardar y buscar registros de auditoría de ajustes de inventario.
 */
public interface IAjusteInventarioAuditoriaRepositorio {

	/**
	 * Guarda un registro de auditoría de ajuste de inventario.
	 * 
	 * @param auditoria el registro de auditoría a guardar
	 * @return el registro de auditoría guardado con su ID generado
	 */
	AjusteInventarioAuditoria guardar(AjusteInventarioAuditoria auditoria);

	/**
	 * Busca un registro de auditoría por el ID del movimiento asociado.
	 * 
	 * @param movimientoId el ID del movimiento de inventario
	 * @return un Optional conteniendo el registro de auditoría si existe, o vacío si no se encuentra
	 */
	Optional<AjusteInventarioAuditoria> buscarPorMovimiento(Long movimientoId);
}
