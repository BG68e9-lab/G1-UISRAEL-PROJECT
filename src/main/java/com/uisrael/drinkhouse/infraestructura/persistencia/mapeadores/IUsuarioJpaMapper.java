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

	@Mapping(target = "fkNegocioEntity", ignore = true)
	@Mapping(target = "fkRolEntity", ignore = true)
	@Mapping(target = "codigosAcceso", ignore = true)
	@Mapping(target = "logsAuditoria", ignore = true)
	UsuarioEntity toEntity(Usuario usuarioDomain);
}
