package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.presentacion.dto.response.AlertaResponseDto;

@Mapper(componentModel = "spring")
public interface IAlertaDtoMapper {

    AlertaResponseDto toResponseDto(Alerta alerta);
}
