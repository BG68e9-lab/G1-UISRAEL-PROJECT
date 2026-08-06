package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ServicioNoDisponibleException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
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
						.orElseGet(() -> {
							SecuenciaCodigo nueva = new SecuenciaCodigo(negocioId, tipoMovimientoId, 0L);
							return repositorio.guardar(nueva);
						});
				
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

	@Override
	@Transactional(readOnly = true)
	public List<SecuenciaCodigo> listarTodas() {
		return repositorio.listarTodas();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SecuenciaCodigo> listarPorNegocio(Integer negocioId) {
		return repositorio.listarPorNegocio(negocioId);
	}

	@Override
	@Transactional(readOnly = true)
	public SecuenciaCodigo buscar(Integer negocioId, Integer tipoMovimientoId) {
		return repositorio.buscarPorNegocioYTipo(negocioId, tipoMovimientoId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Secuencia no encontrada para negocio=" + negocioId
								+ " tipo=" + tipoMovimientoId));
	}

	@Override
	@Transactional
	public SecuenciaCodigo crear(SecuenciaCodigo secuencia) {
		if (repositorio.buscarPorNegocioYTipo(
				secuencia.getNegocioId(), 
				secuencia.getTipoMovimientoId()).isPresent()) {
			throw new ConflictoUnicoException(
					"Ya existe una secuencia para negocio=" + secuencia.getNegocioId()
							+ " tipo=" + secuencia.getTipoMovimientoId());
		}

		if (secuencia.getUltimoNumero() == null) {
			secuencia.setUltimoNumero(0L);
		}

		return repositorio.guardar(secuencia);
	}

	@Override
	@Transactional
	public SecuenciaCodigo actualizar(Integer negocioId, Integer tipoMovimientoId, Long nuevoNumero) {
		SecuenciaCodigo secuencia = buscar(negocioId, tipoMovimientoId);
		secuencia.setUltimoNumero(nuevoNumero);
		return repositorio.guardar(secuencia);
	}

	@Override
	@Transactional
	public void eliminar(Integer negocioId, Integer tipoMovimientoId) {
		SecuenciaCodigo secuencia = buscar(negocioId, tipoMovimientoId);
		repositorio.eliminar(secuencia);
	}

	@Override
	@Transactional
	public SecuenciaCodigo reiniciar(Integer negocioId, Integer tipoMovimientoId, Long valorInicial) {
		return actualizar(negocioId, tipoMovimientoId, valorInicial != null ? valorInicial : 0L);
	}

	@Override
	@Transactional
	public int inicializarSecuenciasParaTodosLosNegocios() {
		List<SecuenciaCodigo> secuenciasExistentes = repositorio.listarTodas();
		int contadorCreadas = 0;
		
		for (int negocioId = 1; negocioId <= 5; negocioId++) {
			for (int tipoId = 1; tipoId <= 4; tipoId++) {
				try {
					final int negocioIdFinal = negocioId;
					final int tipoIdFinal = tipoId;
					
					boolean existe = secuenciasExistentes.stream()
							.anyMatch(s -> s.getNegocioId().equals(negocioIdFinal) 
									&& s.getTipoMovimientoId().equals(tipoIdFinal));
					
					if (!existe) {
						SecuenciaCodigo nueva = new SecuenciaCodigo(negocioIdFinal, tipoIdFinal, 0L);
						crear(nueva);
						contadorCreadas++;
					}
				} catch (Exception e) {
				}
			}
		}
		
		return contadorCreadas;
	}
}
