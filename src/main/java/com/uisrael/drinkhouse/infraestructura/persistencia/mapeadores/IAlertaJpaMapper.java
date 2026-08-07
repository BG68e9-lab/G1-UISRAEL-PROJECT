package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AlertaEntity;

@Mapper(componentModel = "spring")
public interface IAlertaJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	Alerta toDomain(AlertaEntity entity);

	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(domain.getNegocioId()))")
	@Mapping(target = "creadoEn", ignore = true)
	AlertaEntity toEntity(Alerta domain);

default com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity entity = 
			new com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}
}
