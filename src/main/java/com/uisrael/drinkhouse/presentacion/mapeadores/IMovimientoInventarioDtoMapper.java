package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;

/**
 * Mapper MapStruct entre la entidad de dominio MovimientoInventario y sus DTOs REST.
 */
@Mapper(componentModel = "spring")
public interface IMovimientoInventarioDtoMapper {

	/**
	 * Convierte el request DTO a entidad de dominio.
	 * Los IDs de producto, lote y tipo de movimiento se manejan por separado
	 * en el controller y no se mapean aquí directamente.
	 */
	@Mapping(target = "movimientoId",          ignore = true)
	@Mapping(target = "codigoMovimiento",      ignore = true)
	@Mapping(target = "tipoMovimientoCodigo",  ignore = true)
	@Mapping(target = "creadoEn",              ignore = true)
	MovimientoInventario toDomain(MovimientoInventarioRequestDto dto);

	/**
	 * Convierte la entidad de dominio al DTO de respuesta.
	 * El campo tipoMovimiento en el response es el código del tipo.
	 */
	@Mapping(source = "tipoMovimientoCodigo", target = "tipoMovimiento")
	MovimientoInventarioResponseDto toResponseDto(MovimientoInventario movimientoInventario);
}
