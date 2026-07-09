package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;

@Mapper(componentModel = "spring")
public interface ICodigoAccesoJpaMapper {

	CodigoAcceso toDomain(CodigoAccesoEntity codigoAccesoEntity);

	CodigoAccesoEntity toEntity(CodigoAcceso codigoAccesoDomain);
}
