package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoProductoEntity;

@Mapper(componentModel = "spring")
public interface IProductoJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkCategoriaEntity.categoriaId", target = "categoriaId")
	@Mapping(source = "fkCategoriaEntity.nombre", target = "categoriaNombre")
	@Mapping(source = "fkTipoProductoEntity.tipoProductoId", target = "tipoProductoId")
	@Mapping(source = "fkTipoProductoEntity.nombre", target = "tipoProductoNombre")
	Producto toDomain(ProductoEntity productoEntity);

	@Mapping(source = "negocioId", target = "fkNegocioEntity", qualifiedByName = "negocioIdToEntity")
	@Mapping(source = "categoriaId", target = "fkCategoriaEntity", qualifiedByName = "categoriaIdToEntity")
	@Mapping(source = "tipoProductoId", target = "fkTipoProductoEntity", qualifiedByName = "tipoProductoIdToEntity")
	@Mapping(target = "lotes", ignore = true)
	@Mapping(target = "movimientos", ignore = true)
	@Mapping(target = "creadoEn", ignore = true)
	@Mapping(target = "actualizadoEn", ignore = true)
	ProductoEntity toEntity(Producto productoDomain);

	@Named("negocioIdToEntity")
	default NegocioEntity negocioIdToEntity(Integer negocioId) {
		if (negocioId == null) return null;
		NegocioEntity entity = new NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}

	@Named("categoriaIdToEntity")
	default CategoriaEntity categoriaIdToEntity(Long categoriaId) {
		if (categoriaId == null) return null;
		CategoriaEntity entity = new CategoriaEntity();
		entity.setCategoriaId(categoriaId);
		return entity;
	}

	@Named("tipoProductoIdToEntity")
	default TipoProductoEntity tipoProductoIdToEntity(Long tipoProductoId) {
		if (tipoProductoId == null) return null;
		TipoProductoEntity entity = new TipoProductoEntity();
		entity.setTipoProductoId(tipoProductoId);
		return entity;
	}
}
