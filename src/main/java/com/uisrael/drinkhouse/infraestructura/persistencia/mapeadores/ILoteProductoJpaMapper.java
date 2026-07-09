package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;

@Mapper(componentModel = "spring")
public interface ILoteProductoJpaMapper {

	LoteProducto toDomain(LoteProductoEntity loteProductoEntity);

	LoteProductoEntity toEntity(LoteProducto loteProductoDomain);
}
