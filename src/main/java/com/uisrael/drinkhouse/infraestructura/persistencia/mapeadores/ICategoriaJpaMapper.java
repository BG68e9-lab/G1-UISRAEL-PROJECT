package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;

@Mapper(componentModel = "spring")
public interface ICategoriaJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	Categoria toDomain(CategoriaEntity categoriaEntity);

	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(categoriaDomain.getNegocioId()))")
	@Mapping(target = "productos", ignore = true)
	@Mapping(target = "creadoEn", ignore = true)
	@Mapping(target = "actualizadoEn", ignore = true)
	CategoriaEntity toEntity(Categoria categoriaDomain);

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
