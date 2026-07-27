package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Negocio;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;

public class NegocioUseCaseImpl implements INegocioUseCase {

	private final INegocioRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public NegocioUseCaseImpl(INegocioRepositorio repositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public Negocio crearNegocio(Negocio negocio) {
		String ruc = negocio.getRuc();
		if (ruc == null || !ruc.matches("\\d{13}")) {
			throw new ReglaNegocioException("RUC inválido: debe tener 13 dígitos numéricos");
		}
		if (repositorio.existePorRuc(ruc)) {
			throw new ConflictoUnicoException("Ya existe un negocio con RUC: " + ruc);
		}
		if (negocio.getActivo() == null) {
			negocio.setActivo(true);
		}
		Negocio guardado = repositorio.guardar(negocio);
		logAuditoriaUseCase.registrar("Negocio", null, "CREAR", guardado);
		return guardado;
	}

	@Override
	public Negocio actualizarNegocio(Integer id, Negocio negocio) {
		Negocio existente = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Negocio no encontrado con id: " + id));
		existente.setNombre(negocio.getNombre());
		existente.setRuc(negocio.getRuc());
		existente.setActivo(negocio.getActivo());
		Negocio actualizado = repositorio.guardar(existente);
		logAuditoriaUseCase.registrar("Negocio", id.toString(), "ACTUALIZAR", actualizado);
		return actualizado;
	}

	@Override
	public Negocio buscarActivo() {
		return repositorio.buscarActivo()
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe un negocio activo"));
	}

	@Override
	public Negocio buscarPorId(Integer id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Negocio no encontrado con id: " + id));
	}

}
