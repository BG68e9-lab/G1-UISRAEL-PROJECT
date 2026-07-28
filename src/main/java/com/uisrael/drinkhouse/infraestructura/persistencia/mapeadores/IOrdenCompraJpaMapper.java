package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;

@Mapper(componentModel = "spring")
public interface IOrdenCompraJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(target = "codigoReferencia", source = "numeroOc")
	@Mapping(target = "estado", source = "fkEstadoOcEntity.codigo")
	@Mapping(target = "total", source = "totalOc")
	OrdenCompra toDomain(OrdenCompraEntity entity);

	@Mapping(target = "numeroOc", source = "codigoReferencia")
	@Mapping(target = "totalOc", source = "total")
	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(domain.getNegocioId()))")
	@Mapping(target = "fkProveedorEntity", ignore = true)
	@Mapping(target = "fkEstadoOcEntity", ignore = true)
	@Mapping(target = "fechaOc", ignore = true)
	@Mapping(target = "documentoUrl", ignore = true)
	@Mapping(target = "extraidoPorIa", ignore = true)
	@Mapping(target = "lotes", ignore = true)
	@Mapping(target = "identificaciones", ignore = true)
	OrdenCompraEntity toEntity(OrdenCompra domain);

	/**
	 * Crea una referencia a NegocioEntity con solo el ID para evitar null en FK.
	 */
	default com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity entity = 
			new com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}
}
