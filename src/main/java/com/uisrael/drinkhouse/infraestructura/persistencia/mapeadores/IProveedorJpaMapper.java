package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;

@Mapper(componentModel = "spring")
public interface IProveedorJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	Proveedor toDomain(ProveedorEntity proveedorEntity);

	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(proveedorDomain.getNegocioId()))")
	ProveedorEntity toEntity(Proveedor proveedorDomain);

	default NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		NegocioEntity entity = new NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}
}
