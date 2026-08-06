package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria;
import com.uisrael.drinkhouse.presentacion.dto.response.AjusteAuditoriaResponseDto;

/**
 * Mapper MapStruct para convertir entre AjusteInventarioAuditoria (dominio)
 * y AjusteAuditoriaResponseDto (presentación).
 * 
 * Este mapper se utiliza para transformar los registros de auditoría de ajustes
 * de inventario desde la capa de dominio a DTOs de respuesta REST.
 */
@Mapper(componentModel = "spring")
public interface IAjusteAuditoriaDtoMapper {

	/**
	 * Convierte una entidad de dominio AjusteInventarioAuditoria a su DTO de respuesta.
	 * 
	 * @param domain entidad de dominio con los datos de auditoría
	 * @return DTO de respuesta con los datos de auditoría para la capa de presentación
	 */
	@Mapping(target = "productoNombre", ignore = true)
	@Mapping(target = "loteCodigo", ignore = true)
	AjusteAuditoriaResponseDto toResponseDto(AjusteInventarioAuditoria domain);
}
