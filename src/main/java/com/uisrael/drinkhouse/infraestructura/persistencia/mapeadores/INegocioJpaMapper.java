package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;

@Mapper(componentModel = "spring")
public interface INegocioJpaMapper {
	
	Negocio toDomain(NegocioEntity negocioEntity);
	
	NegocioEntity toEntity(Negocio negocioDomain);

}
