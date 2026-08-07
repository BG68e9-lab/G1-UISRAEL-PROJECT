package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.presentacion.dto.response.AjusteAuditoriaResponseDto;

@Mapper(componentModel = "spring")
public interface IAjusteAuditoriaDtoMapper {

@Mapping(target = "productoNombre", ignore = true)
	@Mapping(target = "loteCodigo", ignore = true)
	AjusteAuditoriaResponseDto toResponseDto(AjusteInventarioAuditoria domain);
}
