package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.ILogAuditoriaRepositorio;

public class LogAuditoriaUseCaseImpl implements ILogAuditoriaUseCase {
	
	private final ILogAuditoriaRepositorio repositorio;
	
	public LogAuditoriaUseCaseImpl(ILogAuditoriaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public LogAuditoria guardar(LogAuditoria nuevoLogAuditoria) {
		return repositorio.guardar(nuevoLogAuditoria);
	}

	@Override
	public LogAuditoria buscarPorId(Long idLogAuditoria) {
		return repositorio.buscarPorId(idLogAuditoria)
				.orElseThrow(() -> new RuntimeException("Log auditoria no encontrado"));
	}

	@Override
	public List<LogAuditoria> listarTodos() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(Long idLogAuditoria) {
		repositorio.eliminar(idLogAuditoria);
	}

}
