package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;

@Mapper(componentModel = "spring")
public interface IProveedorJpaMapper {

	Proveedor toDomain(ProveedorEntity proveedorEntity);

	@Mapping(target = "fkNegocioEntity", ignore = true)
	ProveedorEntity toEntity(Proveedor proveedorDomain);
}
