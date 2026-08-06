package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;

/**
 * Mapper MapStruct para convertir entre LoteProductoEntity (JPA) y LoteProducto (dominio).
 */
@Mapper(componentModel = "spring")
public interface ILoteProductoJpaMapper {

	/**
	 * Convierte una entidad JPA a objeto de dominio.
	 *
	 * @param entidad entidad JPA de lote producto
	 * @return objeto de dominio LoteProducto
	 */
	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkProductoEntity.productoId", target = "productoId")
	@Mapping(source = "fkProductoEntity.nombre", target = "productoNombre")
	LoteProducto aDominio(LoteProductoEntity entidad);

	/**
	 * Convierte un objeto de dominio a entidad JPA.
	 * Las relaciones FK se asignan externamente en el adaptador.
	 *
	 * @param dominio objeto de dominio LoteProducto
	 * @return entidad JPA LoteProductoEntity sin relaciones
	 */
	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(dominio.getNegocioId()))")
	@Mapping(target = "fkProductoEntity", ignore = true)
	@Mapping(target = "fkOrdenCompraEntity", ignore = true)
	@Mapping(target = "fkEstadoRespaldoEntity", ignore = true)
	@Mapping(target = "fkUsuarioEntity", ignore = true)
	@Mapping(target = "movimientos", ignore = true)
	@Mapping(target = "creadoEn", ignore = true)
	LoteProductoEntity aEntidad(LoteProducto dominio);

	default NegocioEntity createNegocioEntity(Integer negocioId) {
		if (negocioId == null) return null;
		NegocioEntity entity = new NegocioEntity();
		entity.setNegocioId(negocioId);
		return entity;
	}
}
