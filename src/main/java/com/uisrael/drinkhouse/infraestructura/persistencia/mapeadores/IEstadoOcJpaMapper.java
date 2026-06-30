package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoOcEntity;

@Mapper(componentModel = "spring")
public interface IEstadoOcJpaMapper {
	
	EstadoOc toDomain(EstadoOcEntity estadoOcEntity);
	
	EstadoOcEntity toEntity(EstadoOc estadoOcDomain);

}
