package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.presentacion.dto.request.ProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoResponseDto;


@Mapper(componentModel = "spring")
public interface IProductoDtoMapper {
	
	Producto toDomain(ProductoRequestDto dto);
	ProductoResponseDto toResponseDto(Producto producto);

}
