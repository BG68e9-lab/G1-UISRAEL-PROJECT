package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;
import com.uisrael.drinkhouse.presentacion.dto.response.NotaVentaResponseDto;

@Mapper(componentModel = "spring")
public interface INotaVentaDtoMapper {

	NotaVentaResponseDto toResponseDto(NotaVenta notaVenta);
}
