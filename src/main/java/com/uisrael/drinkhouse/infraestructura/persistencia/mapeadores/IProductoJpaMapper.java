package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;

@Mapper(componentModel = "spring")
public interface IProductoJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkCategoriaEntity.categoriaId", target = "categoriaId")
	Producto toDomain(ProductoEntity productoEntity);


	@Mapping(target = "fkNegocioEntity.negocioId", source = "negocioId")
	@Mapping(target = "fkCategoriaEntity.categoriaId", source = "categoriaId")
	@Mapping(target = "lotes", ignore = true)
	@Mapping(target = "movimientos", ignore = true)
	@Mapping(target = "creadoEn", ignore = true)
	@Mapping(target = "actualizadoEn", ignore = true)
	ProductoEntity toEntity(Producto productoDomain);
}
