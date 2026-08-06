package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;

/**
 * MapStruct mapper between AjusteInventarioAuditoriaEntity (JPA) and AjusteInventarioAuditoria (domain).
 * Converts between infrastructure and domain layers for audit records.
 * 
 * @see AjusteInventarioAuditoria
 * @see AjusteInventarioAuditoriaEntity
 */
@Mapper(componentModel = "spring")
public interface IAjusteInventarioAuditoriaJpaMapper {

	/**
	 * Converts domain audit record to JPA entity.
	 * 
	 * @param domain Domain audit record
	 * @return JPA entity
	 */
	AjusteInventarioAuditoriaEntity toEntity(AjusteInventarioAuditoria domain);

	/**
	 * Converts JPA entity to domain audit record.
	 * 
	 * @param entity JPA entity
	 * @return Domain audit record
	 */
	AjusteInventarioAuditoria toDomain(AjusteInventarioAuditoriaEntity entity);
}
