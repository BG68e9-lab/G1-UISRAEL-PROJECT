package com.uisrael.drinkhouse.presentacion.mapeadores;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.presentacion.dto.response.IdentificacionIaResponseDto;

@Mapper(componentModel = "spring")
public interface IIdentificacionIaDtoMapper {

@Mapping(target = "nombreModelo", source = "modeloIaUsado")
    @Mapping(target = "resultado", source = "nombreSugerido")
    @Mapping(target = "identificacionIaId", source = "identificacionId")
    @Mapping(target = "resultadoProducto", ignore = true)
    @Mapping(target = "resultadoBotella", ignore = true)
    @Mapping(target = "resultadoFactura", ignore = true)
    @Mapping(target = "tipoIdentificacion", ignore = true)
    IdentificacionIaResponseDto aResponseDto(IdentificacionIa dominio);

List<IdentificacionIaResponseDto> aListaResponseDto(List<IdentificacionIa> lista);
}
