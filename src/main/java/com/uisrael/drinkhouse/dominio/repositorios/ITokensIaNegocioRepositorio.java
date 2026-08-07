package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TokensIaNegocio;

public interface ITokensIaNegocioRepositorio {

TokensIaNegocio guardar(TokensIaNegocio tokens);

Optional<TokensIaNegocio> buscarPorNegocioId(Integer negocioId);
}
