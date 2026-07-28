package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface IUsuarioJpaMapper {

	@Mapping(source = "fkNegocioEntity.negocioId", target = "negocioId")
	@Mapping(source = "fkRolEntity.rolId", target = "rolId")
	Usuario toDomain(UsuarioEntity usuarioEntity);

	@Mapping(target = "fkNegocioEntity", expression = "java(createNegocioEntity(usuarioDomain.getNegocioId()))")
	@Mapping(target = "fkRolEntity", expression = "java(createRolEntity(usuarioDomain.getRolId()))")
	@Mapping(target = "codigosAcceso", ignore = true)
	@Mapping(target = "logsAuditoria", ignore = true)
	UsuarioEntity toEntity(Usuario usuarioDomain);

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

	/**
	 * Crea una referencia a RolEntity con solo el ID para evitar null en FK.
	 */
	default com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity createRolEntity(Integer rolId) {
		if (rolId == null) return null;
		com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity entity = 
			new com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity();
		entity.setRolId(rolId);
		return entity;
	}
}
