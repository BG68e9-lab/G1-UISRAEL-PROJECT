package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.DetalleOrdenCompraEntity;

@Mapper(componentModel = "spring")
public interface IDetalleOrdenCompraJpaMapper {

	@Mapping(target = "ordenCompraId", source = "ordenCompraId.ordenCompraId")
	@Mapping(target = "productoId", source = "fkProductoEntity.productoId")
	DetalleOrdenCompra toDomain(DetalleOrdenCompraEntity entity);

	@Mapping(target = "ordenCompraId", ignore = true)
	@Mapping(target = "fkProductoEntity", ignore = true)
	DetalleOrdenCompraEntity toEntity(DetalleOrdenCompra domain);
}
