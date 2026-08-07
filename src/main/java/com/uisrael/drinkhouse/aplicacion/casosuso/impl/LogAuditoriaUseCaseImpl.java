package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.ILogAuditoriaRepositorio;

public class LogAuditoriaUseCaseImpl implements ILogAuditoriaUseCase {

	private final ILogAuditoriaRepositorio repositorio;
	private final ObjectMapper objectMapper;

	public LogAuditoriaUseCaseImpl(ILogAuditoriaRepositorio repositorio, ObjectMapper objectMapper) {
		this.repositorio = repositorio;
		this.objectMapper = objectMapper;
	}

	@Override
	public LogAuditoria registrar(String entidad, String entidadId, String accion, Object detalle) {
		String detalleJson = null;
		if (detalle != null) {
			try {
				detalleJson = objectMapper.writeValueAsString(detalle);
			} catch (JsonProcessingException e) {
				detalleJson = "{\"error\":\"Serialization failed\",\"value\":\"" + 
					detalle.toString().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"}";
			}
		}
		LogAuditoria log = new LogAuditoria(null, entidad, entidadId, accion, detalleJson, OffsetDateTime.now());
		return repositorio.guardar(log);
	}

	@Override
	public List<LogAuditoria> buscarConFiltros(String entidad, String accion,
			OffsetDateTime desde, OffsetDateTime hasta) {
		return repositorio.buscarConFiltros(entidad, accion, desde, hasta);
	}

	@Override
	public List<LogAuditoria> buscarPorEntidadId(String entidadId) {
		return repositorio.buscarPorEntidadId(entidadId);
	}
}
