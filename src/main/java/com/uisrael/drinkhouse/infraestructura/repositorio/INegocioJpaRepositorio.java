package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;

public interface INegocioJpaRepositorio extends JpaRepository<NegocioEntity, Integer> {

	Optional<NegocioEntity> findByActivoTrue();

	boolean existsByRuc(String ruc);

}
