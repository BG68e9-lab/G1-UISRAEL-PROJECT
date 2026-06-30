package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.presentacion.dto.request.CategoriaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CategoriaResponseDto;

@Mapper(componentModel = "spring")
public interface ICategoriaDtoMapper {
	
	Categoria toDomain(CategoriaRequestDto dto);

	CategoriaResponseDto toResponseDto(Categoria categoria);
}
