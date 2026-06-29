package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;

@Mapper(componentModel = "spring")
public interface IMovimientoInventarioDtoMapper {

	MovimientoInventario toDomain(MovimientoInventarioRequestDto dto);
	MovimientoInventarioResponseDto toResponseDto(MovimientoInventario movimientoInventario);
}
