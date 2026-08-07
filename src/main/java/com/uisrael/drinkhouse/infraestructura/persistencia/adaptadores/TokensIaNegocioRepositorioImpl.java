package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;
import com.uisrael.drinkhouse.dominio.repositorios.ITokensIaNegocioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TokensIaNegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITokensIaNegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITokensIaNegocioJpaRepositorio;

public class TokensIaNegocioRepositorioImpl implements ITokensIaNegocioRepositorio {

    private final ITokensIaNegocioJpaRepositorio jpaRepositorio;
    private final ITokensIaNegocioJpaMapper mapper;

    public TokensIaNegocioRepositorioImpl(ITokensIaNegocioJpaRepositorio jpaRepositorio,
                                          ITokensIaNegocioJpaMapper mapper) {
        this.jpaRepositorio = jpaRepositorio;
        this.mapper = mapper;
    }

@Override
    public TokensIaNegocio guardar(TokensIaNegocio tokens) {
        TokensIaNegocioEntity entidad = mapper.aEntidad(tokens);
        NegocioEntity negocioRef = new NegocioEntity();
        negocioRef.setNegocioId(tokens.getNegocioId());
        entidad.setNegocio(negocioRef);
        TokensIaNegocioEntity guardado = jpaRepositorio.save(entidad);
        return mapper.aDominio(guardado);
    }

@Override
    public Optional<TokensIaNegocio> buscarPorNegocioId(Integer negocioId) {
        return jpaRepositorio.findByNegocio_NegocioId(negocioId)
                .map(mapper::aDominio);
    }
}
