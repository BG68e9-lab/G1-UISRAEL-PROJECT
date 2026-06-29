package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;
import com.uisrael.drinkhouse.presentacion.dto.request.EstadoRespaldoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.EstadoRespaldoResponseDto;

@Mapper(componentModel = "spring")
public interface IEstadoRespaldoDtoMapper {
	
	EstadoRespaldo toDomain(EstadoRespaldoRequestDto dto);
	EstadoRespaldoResponseDto toResponseDto(EstadoRespaldo estadoRespaldo);

}