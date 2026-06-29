package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;
import com.uisrael.drinkhouse.presentacion.dto.request.NegocioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.NegocioResponseDto;

@Mapper(componentModel = "spring")
public interface INegocioDtoMapper {

	Negocio toDomain(NegocioRequestDto dto);

	NegocioResponseDto toResponseDto(Negocio negocio);

}
