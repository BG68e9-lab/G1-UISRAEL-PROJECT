package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;

@Mapper(componentModel = "spring")
public interface IMovimientoInventarioDtoMapper {

@Mapping(target = "movimientoId",          ignore = true)
	@Mapping(target = "codigoMovimiento",      ignore = true)
	@Mapping(target = "tipoMovimientoCodigo",  ignore = true)
	@Mapping(target = "creadoEn",              ignore = true)
	MovimientoInventario toDomain(MovimientoInventarioRequestDto dto);

@Mapping(target = "movimientoId",          ignore = true)
	@Mapping(target = "codigoMovimiento",      ignore = true)
	@Mapping(target = "tipoMovimientoCodigo",  ignore = true)
	@Mapping(target = "creadoEn",              ignore = true)
	@Mapping(source = "ventaId",               target = "ventaId")
	@Mapping(source = "cantidadAnterior",      target = "cantidadAnterior")
	@Mapping(source = "ajuste",                target = "ajuste")
	@Mapping(source = "cantidadPosterior",     target = "cantidadPosterior")
	MovimientoInventario toDomainWithAuditFields(MovimientoInventarioRequestDto dto);

@Mapping(source = "tipoMovimientoCodigo", target = "tipoMovimiento")
	MovimientoInventarioResponseDto toResponseDto(MovimientoInventario movimientoInventario);
}
