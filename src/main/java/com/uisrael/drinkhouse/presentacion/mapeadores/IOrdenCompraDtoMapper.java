package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.presentacion.dto.request.DetalleOrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.OrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.DetalleOrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;

/**
 * Mapper MapStruct entre la entidad de dominio OrdenCompra/DetalleOrdenCompra y sus DTOs REST.
 */
@Mapper(componentModel = "spring")
public interface IOrdenCompraDtoMapper {

    /**
     * Convierte el request DTO de orden a entidad de dominio.
     * proveedorId y detalles se manejan por separado en el controller/use case.
     */
    @Mapping(target = "ordenCompraId", ignore = true)
    @Mapping(target = "codigoReferencia", ignore = true)
    @Mapping(target = "estado", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "creadoEn", ignore = true)
    OrdenCompra toDomain(OrdenCompraRequestDto dto);

    /**
     * Convierte la entidad de dominio a DTO de respuesta.
     * proveedorId, proveedorRazonSocial y detalles no están en el dominio OrdenCompra — se ignoran aquí
     * y el controller los completa manualmente si es necesario.
     * fechaCreacion se mapea desde el campo creadoEn del dominio.
     */
    @Mapping(source = "creadoEn", target = "fechaCreacion")
    @Mapping(target = "proveedorId", ignore = true)
    @Mapping(target = "proveedorRazonSocial", ignore = true)
    @Mapping(target = "detalles", ignore = true)
    OrdenCompraResponseDto toResponseDto(OrdenCompra orden);

    /**
     * Convierte el request DTO de detalle a entidad de dominio.
     */
    @Mapping(target = "detalleOcId", ignore = true)
    @Mapping(target = "ordenCompraId", ignore = true)
    DetalleOrdenCompra detalleRequestToDomain(DetalleOrdenCompraRequestDto dto);

    /**
     * Convierte la entidad de dominio detalle a DTO de respuesta.
     * productoNombre no está en el dominio — se completa en el controller.
     */
    @Mapping(target = "productoNombre", ignore = true)
    DetalleOrdenCompraResponseDto detalleToResponseDto(DetalleOrdenCompra detalle);
}
