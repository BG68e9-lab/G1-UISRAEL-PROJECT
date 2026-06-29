package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.presentacion.dto.request.TipoMovimientoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TipoMovimientoResponseDto;

@Mapper(componentModel = "spring")
public interface ITipoMovimientoDtoMapper {
	
	TipoMovimiento toDomain(TipoMovimientoRequestDto dto);
	TipoMovimientoResponseDto toResponseDto(TipoMovimiento tipoMovimiento);

}