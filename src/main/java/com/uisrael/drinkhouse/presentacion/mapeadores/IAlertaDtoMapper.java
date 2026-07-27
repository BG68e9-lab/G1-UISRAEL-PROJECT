package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.presentacion.dto.response.AlertaResponseDto;

/**
 * Mapper MapStruct para convertir entre la entidad de dominio Alerta
 * y su DTO de respuesta REST.
 */
@Mapper(componentModel = "spring")
public interface IAlertaDtoMapper {

    AlertaResponseDto toResponseDto(Alerta alerta);
}
