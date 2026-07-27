package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoOcEntity;

public interface IEstadoOcJpaRepositorio extends JpaRepository<EstadoOcEntity, Integer> {

	Optional<EstadoOcEntity> findByCodigo(String codigo);
}
