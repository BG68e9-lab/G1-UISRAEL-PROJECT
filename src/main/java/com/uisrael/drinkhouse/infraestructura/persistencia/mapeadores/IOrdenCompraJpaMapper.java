package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;

@Mapper(componentModel = "spring")
public interface IOrdenCompraJpaMapper {

	@Mapping(target = "codigoReferencia", source = "numeroOc")
	@Mapping(target = "estado", source = "fkEstadoOcEntity.codigo")
	@Mapping(target = "total", source = "totalOc")
	OrdenCompra toDomain(OrdenCompraEntity entity);

	@Mapping(target = "numeroOc", source = "codigoReferencia")
	@Mapping(target = "totalOc", source = "total")
	@Mapping(target = "fkNegocioEntity", ignore = true)
	@Mapping(target = "fkProveedorEntity", ignore = true)
	@Mapping(target = "fkEstadoOcEntity", ignore = true)
	@Mapping(target = "fechaOc", ignore = true)
	@Mapping(target = "documentoUrl", ignore = true)
	@Mapping(target = "extraidoPorIa", ignore = true)
	@Mapping(target = "confirmadoPor", ignore = true)
	@Mapping(target = "confirmadoEn", ignore = true)
	@Mapping(target = "lotes", ignore = true)
	@Mapping(target = "identificaciones", ignore = true)
	OrdenCompraEntity toEntity(OrdenCompra domain);
}
