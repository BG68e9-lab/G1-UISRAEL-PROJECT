package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;

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
	MovimientoInventario toDomain(MovimientoInventarioEntity entity);

	@Mapping(target = "fkNegocioEntity",       ignore = true)
	@Mapping(target = "fkTipoMovimientoEntity", ignore = true)
	@Mapping(target = "fkProductoEntity",       ignore = true)
	@Mapping(target = "fkLoteEntity",           ignore = true)
	@Mapping(target = "fkUsuarioEntity",        ignore = true)
	MovimientoInventarioEntity toEntity(MovimientoInventario domain);
}
