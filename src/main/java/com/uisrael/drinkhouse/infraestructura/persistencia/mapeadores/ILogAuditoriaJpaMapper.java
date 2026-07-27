package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LogAuditoriaEntity;

@Mapper(componentModel = "spring")
public interface ILogAuditoriaJpaMapper {

	LogAuditoria toDomain(LogAuditoriaEntity logAuditoriaEntity);

	@Mapping(target = "fkNegocioEntity", ignore = true)
	@Mapping(target = "fkUsuarioEntity", ignore = true)
	LogAuditoriaEntity toEntity(LogAuditoria logAuditoriaDomain);
}
