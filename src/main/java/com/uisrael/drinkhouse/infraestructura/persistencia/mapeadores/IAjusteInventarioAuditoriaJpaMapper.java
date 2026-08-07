package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;

@Mapper(componentModel = "spring")
public interface IAjusteInventarioAuditoriaJpaMapper {

AjusteInventarioAuditoriaEntity toEntity(AjusteInventarioAuditoria domain);

AjusteInventarioAuditoria toDomain(AjusteInventarioAuditoriaEntity entity);
}
