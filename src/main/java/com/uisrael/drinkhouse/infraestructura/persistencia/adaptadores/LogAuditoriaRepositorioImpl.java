package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

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
	public LogAuditoria guardar(LogAuditoria nuevoLogAuditoria) {
		LogAuditoriaEntity entity = logAuditoriaMapper.toEntity(nuevoLogAuditoria);
		LogAuditoriaEntity guardado = jpaRepositorio.save(entity);
		return logAuditoriaMapper.toDomain(guardado);
	}

	@Override
	public Optional<LogAuditoria> buscarPorId(Long idLogAuditoria) {
		return jpaRepositorio.findById(idLogAuditoria).map(logAuditoriaMapper::toDomain);
	}

	@Override
	public List<LogAuditoria> listarTodos() {
		return jpaRepositorio.findAll().stream().map(logAuditoriaMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Long idLogAuditoria) {
		jpaRepositorio.deleteById(idLogAuditoria);
	}

}
