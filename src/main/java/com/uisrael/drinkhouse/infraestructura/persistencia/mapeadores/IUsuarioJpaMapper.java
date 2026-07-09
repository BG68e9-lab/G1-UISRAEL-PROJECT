package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;

@Mapper(componentModel = "spring")
public interface IUsuarioJpaMapper {

	Usuario toDomain(UsuarioEntity usuarioEntity);

	UsuarioEntity toEntity(Usuario usuarioDomain);
}
