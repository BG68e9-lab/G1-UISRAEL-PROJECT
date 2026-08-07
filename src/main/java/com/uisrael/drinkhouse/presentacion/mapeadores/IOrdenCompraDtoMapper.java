package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.presentacion.dto.request.DetalleOrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.OrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.DetalleOrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;

@Mapper(componentModel = "spring")
public interface IOrdenCompraDtoMapper {

@Mapping(target = "ordenCompraId", ignore = true)
    @Mapping(target = "codigoReferencia", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "confirmadoPor", ignore = true)
    @Mapping(target = "confirmadoEn", ignore = true)
    OrdenCompra toDomain(OrdenCompraRequestDto dto);

@Mapping(target = "proveedorRazonSocial", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    OrdenCompraResponseDto toResponseDto(OrdenCompra orden);

@Mapping(target = "detalleOcId", ignore = true)
    @Mapping(target = "ordenCompraId", ignore = true)
    DetalleOrdenCompra detalleRequestToDomain(DetalleOrdenCompraRequestDto dto);

@Mapping(target = "productoNombre", ignore = true)
    DetalleOrdenCompraResponseDto detalleToResponseDto(DetalleOrdenCompra detalle);
}
