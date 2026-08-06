package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAjusteInventarioAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio;

/**
 * Implementación del caso de uso para la gestión de auditoría de ajustes de inventario.
 * Proporciona acceso de solo lectura a los registros de auditoría.
 */
@Service
@Transactional(readOnly = true)
public class AjusteInventarioAuditoriaUseCaseImpl implements IAjusteInventarioAuditoriaUseCase {

	private static final Logger logger = LoggerFactory.getLogger(AjusteInventarioAuditoriaUseCaseImpl.class);

	private final IAjusteInventarioAuditoriaRepositorio repositorio;

	public AjusteInventarioAuditoriaUseCaseImpl(IAjusteInventarioAuditoriaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public AjusteInventarioAuditoria buscarPorMovimiento(Long movimientoId) {
		logger.info("Consultando registro de auditoría para movimiento ID: {}", movimientoId);
		
		try {
			AjusteInventarioAuditoria resultado = repositorio.buscarPorMovimiento(movimientoId)
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"No se encontró registro de auditoría para el movimiento " + movimientoId));
			
			logger.info("Registro de auditoría encontrado - Movimiento ID: {}, Usuario ejecutor: {}, Usuario autorizado: {}", 
					movimientoId, resultado.getUsuarioEjecutor(), resultado.getUsuarioAutorizado());
			
			return resultado;
		} catch (RecursoNoEncontradoException e) {
			logger.warn("No se encontró registro de auditoría para movimiento ID: {}", movimientoId);
			throw e;
		}
	}
}
