package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.presentacion.dto.request.LoteProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoteProductoResponseDto;

@Mapper(componentModel = "spring")
public interface ILoteProductoDtoMapper {

@Mapping(target = "loteId", ignore = true)
	@Mapping(target = "productoId", ignore = true)
	@Mapping(target = "productoNombre", ignore = true)
	@Mapping(target = "codigoEntrada", ignore = true)
	@Mapping(target = "cantidadDisponible", ignore = true)
	@Mapping(target = "fechaIngreso", ignore = true)
	@Mapping(target = "negocioId", ignore = true)
	LoteProducto aDominio(LoteProductoRequestDto dto);

	LoteProductoResponseDto aResponseDto(LoteProducto loteProducto);
}
