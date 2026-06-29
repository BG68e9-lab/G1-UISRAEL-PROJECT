package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.presentacion.dto.request.CodigoAccesoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CodigoAccesoResponseDto;

@Mapper(componentModel = "spring")
public interface ICodigoAccesoDtoMapper {
	
	CodigoAcceso toDomain(CodigoAccesoRequestDto dto);
	
	CodigoAccesoResponseDto toResponseDto(CodigoAcceso codigoAcceso);

}
