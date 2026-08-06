package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;
import com.uisrael.drinkhouse.presentacion.dto.response.NotaVentaResponseDto;

/**
 * Mapper MapStruct para convertir entre la entidad de dominio NotaVenta
 * y su DTO de respuesta REST.
 */
@Mapper(componentModel = "spring")
public interface INotaVentaDtoMapper {

	NotaVentaResponseDto toResponseDto(NotaVenta notaVenta);
}
