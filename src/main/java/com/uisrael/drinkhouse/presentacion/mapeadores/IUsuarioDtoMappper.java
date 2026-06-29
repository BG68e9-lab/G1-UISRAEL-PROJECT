package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.mapstruct.Mapper;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.presentacion.dto.request.UsuarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.UsuarioResponseDto;

@Mapper(componentModel = "spring")
public interface IUsuarioDtoMappper {

	Usuario toDomain(UsuarioRequestDto dto);
	
	UsuarioResponseDto toResponseDto(Usuario usuario);
}
