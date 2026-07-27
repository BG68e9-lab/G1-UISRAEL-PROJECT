package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TokensIaNegocioEntity;

@Mapper(componentModel = "spring")
public interface ITokensIaNegocioJpaMapper {

    @Mapping(source = "negocio.negocioId", target = "negocioId")
    TokensIaNegocio aDominio(TokensIaNegocioEntity entidad);

    @Mapping(target = "negocio", ignore = true)
    TokensIaNegocioEntity aEntidad(TokensIaNegocio dominio);
}
