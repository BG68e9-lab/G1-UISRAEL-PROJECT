package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.presentacion.dto.request.ProveedorRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProveedorResponseDto;


@Mapper(componentModel = "spring")

public interface IProveedorDtoMapper {
	
	Proveedor toDomain(ProveedorRequestDto dto);
	ProveedorResponseDto toResponseDto(Proveedor proveedor);



}
