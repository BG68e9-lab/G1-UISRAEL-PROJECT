package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity;

@Mapper(componentModel = "spring")
public interface IRolJpaMapper {

	Rol toDomain(RolEntity rolEntity);

	RolEntity toEntity(Rol rolDomain);
}
