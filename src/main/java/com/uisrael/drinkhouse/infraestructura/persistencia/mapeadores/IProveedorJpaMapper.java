package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;

@Mapper(componentModel = "spring")
public interface IProveedorJpaMapper {

	Proveedor toDomain(ProveedorEntity proveedorEntity);

	ProveedorEntity toEntity(Proveedor proveedorDomain);
}
