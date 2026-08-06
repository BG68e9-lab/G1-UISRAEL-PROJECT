package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;
import com.uisrael.drinkhouse.presentacion.dto.request.TipoProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TipoProductoResponseDto;

@Mapper(componentModel = "spring")
public interface ITipoProductoDtoMapper {

	TipoProducto toDomain(TipoProductoRequestDto dto);

	TipoProductoResponseDto toResponseDto(TipoProducto tipoProducto);
}
