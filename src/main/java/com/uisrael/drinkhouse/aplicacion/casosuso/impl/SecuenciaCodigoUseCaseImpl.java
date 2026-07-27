package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ServicioNoDisponibleException;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;

public class SecuenciaCodigoUseCaseImpl implements ISecuenciaCodigoUseCase {

	private final ISecuenciaCodigoRepositorio repositorio;

	public SecuenciaCodigoUseCaseImpl(ISecuenciaCodigoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	@Transactional
	public Long siguiente(Integer negocioId, Integer tipoMovimientoId) {
		int intentos = 0;
		while (intentos < 3) {
			try {
				SecuenciaCodigo seq = repositorio
						.buscarPorNegocioYTipo(negocioId, tipoMovimientoId)
						.orElseThrow(() -> new RecursoNoEncontradoException(
								"Secuencia no encontrada para negocio=" + negocioId
										+ " tipo=" + tipoMovimientoId));
				long numero = seq.getUltimoNumero() + 1;
				seq.setUltimoNumero(numero);
				repositorio.guardar(seq);
				return numero;
			} catch (OptimisticLockingFailureException ex) {
				intentos++;
			}
		}
		throw new ServicioNoDisponibleException("No se pudo generar secuencia tras 3 intentos");
	}
}
