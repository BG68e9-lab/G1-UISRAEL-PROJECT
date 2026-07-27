package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.presentacion.dto.request.RolRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.RolResponseDto;

@Mapper(componentModel = "spring")
public interface IRolDtoMapper {
	
	Rol toDomain(RolRequestDto dto);
	
	RolResponseDto toResponseDto(Rol rol);

}
