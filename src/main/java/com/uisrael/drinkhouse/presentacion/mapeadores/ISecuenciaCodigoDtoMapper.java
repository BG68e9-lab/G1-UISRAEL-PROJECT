package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.presentacion.dto.request.SecuenciaCodigoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.SecuenciaCodigoResponseDto;

@Mapper(componentModel = "spring")
public interface ISecuenciaCodigoDtoMapper {
	
	SecuenciaCodigo toDomain(SecuenciaCodigoRequestDto dto);
	SecuenciaCodigoResponseDto toResponseDto(SecuenciaCodigo secuenciaCodigo);

}