package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;

public class CodigoAccesoUseCaseImpl implements ICodigoAccesoUseCase {

	private final ICodigoAccesoRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public CodigoAccesoUseCaseImpl(ICodigoAccesoRepositorio repositorio,
			ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public CodigoAcceso generarCodigo(String tipoCodigo, UUID usuarioId) {
		CodigoAcceso codigo = new CodigoAcceso();
		codigo.setCodigoHash(UUID.randomUUID().toString());
		codigo.setTipoCodigo(tipoCodigo);
		codigo.setExpiraEn(OffsetDateTime.now().plusHours(24));
		codigo.setUsado(false);
		CodigoAcceso guardado = repositorio.guardar(codigo);
		logAuditoriaUseCase.registrar("CodigoAcceso", guardado.getCodigoAccesoId().toString(),
				"CREAR", guardado);
		return guardado;
	}

	@Override
	public CodigoAcceso validarCodigo(String codigoHash) {
		CodigoAcceso codigo = repositorio.buscarPorHash(codigoHash)
				.orElseThrow(() -> new  com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException("Código de acceso no encontrado"));

		if (Boolean.TRUE.equals(codigo.getUsado())) {
			throw new ReglaNegocioException("El código ya fue utilizado");
		}
		if (codigo.getExpiraEn() != null && codigo.getExpiraEn().isBefore(OffsetDateTime.now())) {
			throw new ReglaNegocioException("El código está vencido");
		}

		codigo.setUsado(true);
		codigo.setUsadoEn(OffsetDateTime.now());
		CodigoAcceso actualizado = repositorio.guardar(codigo);
		logAuditoriaUseCase.registrar("CodigoAcceso", actualizado.getCodigoAccesoId().toString(),
				"USAR", actualizado);
		return actualizado;
	}
}
