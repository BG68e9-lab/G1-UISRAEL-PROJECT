package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;

@Mapper(componentModel = "spring")
public interface ISecuenciaCodigoJpaMapper {

	SecuenciaCodigo toDomain(SecuenciaCodigoEntity secuenciaCodigoEntity);

	SecuenciaCodigoEntity toEntity(SecuenciaCodigo secuenciaCodigoDomain);

}
