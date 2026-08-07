package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IAjusteInventarioAuditoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IAjusteInventarioAuditoriaJpaRepositorio;

public class AjusteInventarioAuditoriaRepositorioImpl implements IAjusteInventarioAuditoriaRepositorio {

	private final IAjusteInventarioAuditoriaJpaRepositorio jpaRepositorio;
	private final IAjusteInventarioAuditoriaJpaMapper mapper;

public AjusteInventarioAuditoriaRepositorioImpl(
			IAjusteInventarioAuditoriaJpaRepositorio jpaRepositorio,
			IAjusteInventarioAuditoriaJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public AjusteInventarioAuditoria guardar(AjusteInventarioAuditoria auditoria) {
		AjusteInventarioAuditoriaEntity entity = mapper.toEntity(auditoria);
		AjusteInventarioAuditoriaEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public Optional<AjusteInventarioAuditoria> buscarPorMovimiento(Long movimientoId) {
		Optional<AjusteInventarioAuditoriaEntity> entityOptional = jpaRepositorio.findByMovimientoId(movimientoId);
		return entityOptional.map(mapper::toDomain);
	}
}
