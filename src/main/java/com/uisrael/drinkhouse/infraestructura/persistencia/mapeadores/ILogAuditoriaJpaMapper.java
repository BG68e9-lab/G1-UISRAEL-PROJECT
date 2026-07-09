package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LogAuditoriaEntity;

@Mapper(componentModel = "spring")
public interface ILogAuditoriaJpaMapper {

	LogAuditoria toDomain(LogAuditoriaEntity logAuditoriaEntity);

	LogAuditoriaEntity toEntity(LogAuditoria logAuditoriaDomain);
}
