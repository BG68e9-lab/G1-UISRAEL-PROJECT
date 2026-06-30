package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;

@Mapper(componentModel = "spring")
public interface ICategoriaJpaMapper {

	Categoria toDomain(CategoriaEntity categoriaEntity);

	CategoriaEntity toEntity(Categoria categoriaDomain);
}
