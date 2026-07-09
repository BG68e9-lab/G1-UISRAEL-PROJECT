package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;

@Mapper(componentModel = "spring")
public interface IMovimientoInventarioJpaMapper {

	MovimientoInventario toDomain(MovimientoInventarioEntity movimientoInventarioEntity);

	MovimientoInventarioEntity toEntity(MovimientoInventario movimientoInventarioDomain);
}
