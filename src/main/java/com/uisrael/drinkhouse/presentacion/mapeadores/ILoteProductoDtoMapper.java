package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.presentacion.dto.request.LoteProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoteProductoResponseDto;

@Mapper(componentModel = "spring")
public interface ILoteProductoDtoMapper {
	
	
	LoteProducto toDomain(LoteProductoRequestDto dto);
	LoteProductoResponseDto toResponseDto (LoteProducto loteProducto);

}
