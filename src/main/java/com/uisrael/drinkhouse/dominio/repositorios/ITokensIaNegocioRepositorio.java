package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;

/**
 * Puerto de salida para el repositorio de TokensIaNegocio.
 */
public interface ITokensIaNegocioRepositorio {

    /**
     * Persiste la configuración de tokens IA de un negocio.
     *
     * @param tokens entidad a guardar
     * @return entidad persistida
     */
    TokensIaNegocio guardar(TokensIaNegocio tokens);

    /**
     * Busca la configuración de tokens IA de un negocio por su ID.
     *
     * @param negocioId ID del negocio
     * @return Optional con la configuración si existe
     */
    Optional<TokensIaNegocio> buscarPorNegocioId(Integer negocioId);
}
