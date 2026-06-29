package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoRespaldoEntity;

@Mapper(componentModel = "spring")
public interface IEstadoRespaldoJpaMapper {

	EstadoRespaldo toDomain(EstadoRespaldoEntity estadoRespaldoEntity);

	EstadoRespaldoEntity toEntity(EstadoRespaldo estadoRespaldoDomain);

}
