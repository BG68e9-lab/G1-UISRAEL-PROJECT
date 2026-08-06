package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;

/**
 * Mapper MapStruct entre MovimientoInventarioEntity (JPA) y MovimientoInventario (dominio).
 * Extrae los IDs de las entidades relacionadas hacia los campos planos del dominio.
 */
@Mapper(componentModel = "spring")
public interface IMovimientoInventarioJpaMapper {

	@Mapping(source = "fkProductoEntity.productoId", target = "productoId")
	@Mapping(source = "fkLoteEntity.loteId",         target = "loteId")
	@Mapping(source = "fkTipoMovimientoEntity.tipoMovimientoId", target = "tipoMovimientoId")
	@Mapping(source = "fkTipoMovimientoEntity.codigo", target = "tipoMovimientoCodigo")
	@Mapping(source = "ventaId", target = "ventaId")
	@Mapping(target = "cantidadAnterior", ignore = true)
	@Mapping(target = "ajuste", ignore = true)
	@Mapping(target = "cantidadPosterior", ignore = true)
	MovimientoInventario toDomain(MovimientoInventarioEntity entity);

	@Mapping(target = "fkNegocioEntity", ignore = true)
	@Mapping(target = "fkUsuarioEntity", ignore = true)
	@Mapping(target = "fkTipoMovimientoEntity", expression = "java(createTipoMovimientoEntity(domain.getTipoMovimientoId()))")
	@Mapping(target = "fkProductoEntity", expression = "java(createProductoEntity(domain.getProductoId()))")
	@Mapping(target = "fkLoteEntity", expression = "java(createLoteEntity(domain.getLoteId()))")
	@Mapping(source = "ventaId", target = "ventaId")
	MovimientoInventarioEntity toEntity(MovimientoInventario domain);

	default TipoMovimientoEntity createTipoMovimientoEntity(Long tipoMovimientoId) {
		if (tipoMovimientoId == null) return null;
		TipoMovimientoEntity entity = new TipoMovimientoEntity();
		entity.setTipoMovimientoId(tipoMovimientoId.intValue());
		return entity;
	}

	default ProductoEntity createProductoEntity(Long productoId) {
		if (productoId == null) return null;
		ProductoEntity entity = new ProductoEntity();
		entity.setProductoId(productoId);
		return entity;
	}

	default LoteProductoEntity createLoteEntity(Long loteId) {
		if (loteId == null) return null;
		LoteProductoEntity entity = new LoteProductoEntity();
		entity.setLoteId(loteId);
		return entity;
	}
}
