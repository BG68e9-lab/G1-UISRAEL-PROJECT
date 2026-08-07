package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;

@Mapper(componentModel = "spring")
public interface ILoteProductoJpaMapper {

@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkProductoEntity.productoId", target = "productoId")
	@Mapping(source = "fkProductoEntity.nombre", target = "productoNombre")
	LoteProducto aDominio(LoteProductoEntity entidad);

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
