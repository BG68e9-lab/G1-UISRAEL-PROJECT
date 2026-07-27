package com.uisrael.drinkhouse.presentacion.mapeadores;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.presentacion.dto.response.IdentificacionIaResponseDto;

/**
 * Mapper MapStruct para convertir entre IdentificacionIa (dominio) y IdentificacionIaResponseDto.
 */
@Mapper(componentModel = "spring")
public interface IIdentificacionIaDtoMapper {

    /**
     * Convierte una entidad de dominio a DTO de respuesta.
     * Los campos resultadoBotella, resultadoFactura y tipoIdentificacion
     * se ignoran aquí porque se rellenan en el controlador con la respuesta de Claude.
     */
    @Mapping(target = "nombreModelo", source = "modeloIaUsado")
    @Mapping(target = "resultado", source = "nombreSugerido")
    @Mapping(target = "identificacionIaId", source = "identificacionId")
    @Mapping(target = "resultadoBotella", ignore = true)
    @Mapping(target = "resultadoFactura", ignore = true)
    @Mapping(target = "tipoIdentificacion", ignore = true)
    IdentificacionIaResponseDto aResponseDto(IdentificacionIa dominio);

    /**
     * Convierte una lista de entidades de dominio a lista de DTOs de respuesta.
     */
    List<IdentificacionIaResponseDto> aListaResponseDto(List<IdentificacionIa> lista);
}
