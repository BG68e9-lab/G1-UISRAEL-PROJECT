package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;

public interface ITipoMovimientoJpaRepositorio extends JpaRepository<TipoMovimientoEntity, Integer> {

	boolean existsByCodigo(String codigo);

	Optional<TipoMovimientoEntity> findByCodigo(String codigo);
}
