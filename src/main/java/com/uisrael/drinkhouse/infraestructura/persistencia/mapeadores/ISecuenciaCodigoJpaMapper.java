package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;

@Mapper(componentModel = "spring")
public interface ISecuenciaCodigoJpaMapper {

	@Mapping(source = "negocio.negocioId", target = "negocioId")
	@Mapping(source = "tipoMovimiento.tipoMovimientoId", target = "tipoMovimientoId")
	SecuenciaCodigo toDomain(SecuenciaCodigoEntity entity);

	@Mapping(target = "negocio", ignore = true)
	@Mapping(target = "tipoMovimiento", ignore = true)
	@Mapping(target = "version", ignore = true)
	SecuenciaCodigoEntity toEntity(SecuenciaCodigo domain);
}
