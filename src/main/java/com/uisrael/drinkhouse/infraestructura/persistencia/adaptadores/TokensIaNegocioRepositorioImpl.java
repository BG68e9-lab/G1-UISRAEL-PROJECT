package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;
import com.uisrael.drinkhouse.dominio.repositorios.ITokensIaNegocioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TokensIaNegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITokensIaNegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITokensIaNegocioJpaRepositorio;

/**
 * Adaptador de repositorio para TokensIaNegocio.
 * Implementa el puerto de salida ITokensIaNegocioRepositorio usando Spring Data JPA.
 */
public class TokensIaNegocioRepositorioImpl implements ITokensIaNegocioRepositorio {

    private final ITokensIaNegocioJpaRepositorio jpaRepositorio;
    private final ITokensIaNegocioJpaMapper mapper;

    public TokensIaNegocioRepositorioImpl(ITokensIaNegocioJpaRepositorio jpaRepositorio,
                                          ITokensIaNegocioJpaMapper mapper) {
        this.jpaRepositorio = jpaRepositorio;
        this.mapper = mapper;
    }

    /**
     * Persiste la configuración de tokens IA asignando la relación con el negocio.
     */
    @Override
    public TokensIaNegocio guardar(TokensIaNegocio tokens) {
        TokensIaNegocioEntity entidad = mapper.aEntidad(tokens);
        // Asignar referencia al negocio
        NegocioEntity negocioRef = new NegocioEntity();
        negocioRef.setNegocioId(tokens.getNegocioId());
        entidad.setNegocio(negocioRef);
        TokensIaNegocioEntity guardado = jpaRepositorio.save(entidad);
        return mapper.aDominio(guardado);
    }

    /**
     * Busca la configuración de tokens por ID de negocio.
     */
    @Override
    public Optional<TokensIaNegocio> buscarPorNegocioId(Integer negocioId) {
        return jpaRepositorio.findByNegocio_NegocioId(negocioId)
                .map(mapper::aDominio);
    }
}
