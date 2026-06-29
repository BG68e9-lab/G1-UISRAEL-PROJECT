package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;

@Mapper(componentModel = "spring")
public interface ITipoMovimientoJpaMapper {

	TipoMovimiento toDomain(TipoMovimientoEntity tipoMovimientoentity);

	TipoMovimientoEntity toEntity(TipoMovimiento tipoMovimientoDomain);

}
