package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;

@Mapper(componentModel = "spring")
public interface IOrdenCompraJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkProveedorEntity.proveedorId", target = "proveedorId")
	@Mapping(target = "codigoReferencia", source = "numeroOc")
	@Mapping(target = "estado", source = "fkEstadoOcEntity.codigo")
	@Mapping(target = "total", source = "totalOc")
	OrdenCompra toDomain(OrdenCompraEntity entity);

	@Mapping(target = "numeroOc", source = "codigoReferencia")
	@Mapping(target = "totalOc", source = "total")
	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(domain.getNegocioId()))")
	@Mapping(target = "fkProveedorEntity", expression = "java(createProveedorEntity(domain.getProveedorId()))")
	@Mapping(target = "fkEstadoOcEntity", ignore = true)
	@Mapping(target = "fechaOc", ignore = true)
	@Mapping(target = "documentoUrl", ignore = true)
	@Mapping(target = "extraidoPorIa", ignore = true)
	@Mapping(target = "lotes", ignore = true)
	@Mapping(target = "identificaciones", ignore = true)
	OrdenCompraEntity toEntity(OrdenCompra domain);

default com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity entity = 
			new com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}

default com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity createProveedorEntity(Long proveedorId) {
		if (proveedorId == null) return null;
		com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity entity = 
			new com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity();
		entity.setProveedorId(proveedorId);
		return entity;
	}
}
