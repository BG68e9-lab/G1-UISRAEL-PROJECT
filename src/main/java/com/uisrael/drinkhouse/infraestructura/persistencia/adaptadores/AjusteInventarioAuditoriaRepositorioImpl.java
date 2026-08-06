package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IAjusteInventarioAuditoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IAjusteInventarioAuditoriaJpaRepositorio;

/**
 * Implementación del repositorio de AjusteInventarioAuditoria que adapta
 * Spring Data JPA al dominio.
 * 
 * Este adaptador convierte entre entidades de dominio y entidades JPA
 * para operaciones de persistencia de registros de auditoría de ajustes de inventario.
 */
@Repository
public class AjusteInventarioAuditoriaRepositorioImpl implements IAjusteInventarioAuditoriaRepositorio {

	private final IAjusteInventarioAuditoriaJpaRepositorio jpaRepositorio;
	private final IAjusteInventarioAuditoriaJpaMapper mapper;

	/**
	 * Constructor con inyección de dependencias.
	 * 
	 * @param jpaRepositorio Repositorio JPA de Spring Data
	 * @param mapper Mapeador MapStruct entre entidades JPA y dominio
	 */
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
