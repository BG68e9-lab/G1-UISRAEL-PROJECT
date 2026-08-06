package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoProductoEntity;

@Mapper(componentModel = "spring")
public interface ITipoProductoJpaMapper {

	@Mapping(source = "fkCategoriaEntity.categoriaId", target = "categoriaId")
	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	TipoProducto toDomain(TipoProductoEntity tipoProductoEntity);

	@Mapping(target = "fkCategoriaEntity", expression = "java(createCategoriaEntity(tipoProductoDomain.getCategoriaId()))")
	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(tipoProductoDomain.getNegocioId()))")
	@Mapping(target = "productos", ignore = true)
	@Mapping(target = "creadoEn", ignore = true)
	@Mapping(target = "actualizadoEn", ignore = true)
	TipoProductoEntity toEntity(TipoProducto tipoProductoDomain);

	/**
	 * Crea una referencia a CategoriaEntity con solo el ID para evitar null en FK.
	 */
	default CategoriaEntity createCategoriaEntity(Long categoriaId) {
		if (categoriaId == null) return null;
		CategoriaEntity entity = new CategoriaEntity();
		entity.setCategoriaId(categoriaId);
		return entity;
	}

	/**
	 * Crea una referencia a NegocioEntity con solo el ID para evitar null en FK.
	 */
	default NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		NegocioEntity entity = new NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}
}
