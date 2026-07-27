package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.ILogAuditoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LogAuditoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILogAuditoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILogAuditoriaJpaRepositorio;

public class LogAuditoriaRepositorioImpl implements ILogAuditoriaRepositorio {

	private final ILogAuditoriaJpaRepositorio jpaRepositorio;
	private final ILogAuditoriaJpaMapper logAuditoriaMapper;

	public LogAuditoriaRepositorioImpl(ILogAuditoriaJpaRepositorio jpaRepositorio,
			ILogAuditoriaJpaMapper logAuditoriaMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.logAuditoriaMapper = logAuditoriaMapper;
	}

	@Override
	public LogAuditoria guardar(LogAuditoria log) {
		LogAuditoriaEntity entity = logAuditoriaMapper.toEntity(log);
		LogAuditoriaEntity guardado = jpaRepositorio.save(entity);
		return logAuditoriaMapper.toDomain(guardado);
	}

	@Override
	public List<LogAuditoria> buscarConFiltros(String entidad, String accion,
			OffsetDateTime desde, OffsetDateTime hasta) {
		return jpaRepositorio.buscarConFiltros(entidad, accion, desde, hasta)
				.stream()
				.map(logAuditoriaMapper::toDomain)
				.toList();
	}

	@Override
	public List<LogAuditoria> buscarPorEntidadId(String entidadId) {
		return jpaRepositorio.findByEntidadId(entidadId)
				.stream()
				.map(logAuditoriaMapper::toDomain)
				.toList();
	}

}
