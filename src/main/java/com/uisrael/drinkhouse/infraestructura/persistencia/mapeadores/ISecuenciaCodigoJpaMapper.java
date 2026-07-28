package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;

@Mapper(componentModel = "spring")
public interface ISecuenciaCodigoJpaMapper {

	@Mapping(source = "negocio.negocioId", target = "negocioId")
	@Mapping(source = "tipoMovimiento.tipoMovimientoId", target = "tipoMovimientoId")
	SecuenciaCodigo toDomain(SecuenciaCodigoEntity entity);

	@Mapping(target = "negocio", expression = "java(createNegocioEntity(domain.getNegocioId()))")
	@Mapping(target = "tipoMovimiento", expression = "java(createTipoMovimientoEntity(domain.getTipoMovimientoId()))")
	@Mapping(target = "version", ignore = true)
	SecuenciaCodigoEntity toEntity(SecuenciaCodigo domain);

	/**
	 * Crea una referencia a NegocioEntity solo con el ID.
	 * JPA la usará para el JOIN sin cargar toda la entidad.
	 */
	default NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		NegocioEntity entity = new NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}

	/**
	 * Crea una referencia a TipoMovimientoEntity solo con el ID.
	 * JPA la usará para el JOIN sin cargar toda la entidad.
	 */
	default TipoMovimientoEntity createTipoMovimientoEntity(Integer tipoMovimientoId) {
		if (tipoMovimientoId == null) return null;
		TipoMovimientoEntity entity = new TipoMovimientoEntity();
		entity.setTipoMovimientoId(tipoMovimientoId);
		return entity;
	}
}
