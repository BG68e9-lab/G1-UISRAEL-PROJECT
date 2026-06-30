package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.LogAuditoria;
import com.uisrael.drinkhouse.presentacion.dto.request.LogAuditoriaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LogAuditoriaResponseDto;

@Mapper(componentModel = "spring")
public interface ILogAuditoriaDtoMapper {
	
	LogAuditoria toDomain(LogAuditoriaRequestDto dto);
	
	LogAuditoriaResponseDto toResponseDto(LogAuditoria logAuditoria);
}
