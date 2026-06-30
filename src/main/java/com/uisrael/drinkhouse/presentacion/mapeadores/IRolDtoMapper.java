package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.presentacion.dto.request.RolRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.RolResponsetDto;

@Mapper(componentModel = "spring")
public interface IRolDtoMapper {
	
	Rol toDomain(RolRequestDto dto);
	
	RolResponsetDto toResponseDto(Rol rol);

}
