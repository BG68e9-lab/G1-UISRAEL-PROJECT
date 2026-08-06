package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;

@Mapper(componentModel = "spring")
public interface ICodigoAccesoJpaMapper {

	@Mapping(source = "fkUsuarioEntity.usuarioId", target = "usuarioId")
	CodigoAcceso toDomain(CodigoAccesoEntity codigoAccesoEntity);

	@Mapping(source = "usuarioId", target = "fkUsuarioEntity.usuarioId")
	CodigoAccesoEntity toEntity(CodigoAcceso codigoAccesoDomain);
}
