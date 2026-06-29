package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;
import com.uisrael.drinkhouse.presentacion.dto.request.EstadoOcRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.EstadoOcResponseDto;

@Mapper(componentModel = "spring")
public interface EstadoOcDtoMapper {

	EstadoOc toDOmain(EstadoOcRequestDto dto);

	EstadoOcResponseDto toResponseDto(EstadoOc estadoOc);

}
