package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.presentacion.dto.request.DetalleOrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.OrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.DetalleOrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;

@Mapper(componentModel = "spring")
public interface IOrdenCompraDtoMapper {

	OrdenCompra toDomain(OrdenCompraRequestDto dto);

	DetalleOrdenCompra toDomain(DetalleOrdenCompraRequestDto dto);

	OrdenCompraResponseDto toResponseDto(OrdenCompra ordenCompra);

	DetalleOrdenCompraResponseDto toResponseDto(DetalleOrdenCompra detalleOrdenCompra);
}
