package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TokensIaNegocioEntity;

public interface ITokensIaNegocioJpaRepositorio extends JpaRepository<TokensIaNegocioEntity, UUID> {

    Optional<TokensIaNegocioEntity> findByNegocio_NegocioId(Integer negocioId);
}
