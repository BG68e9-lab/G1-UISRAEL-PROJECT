package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;

public interface ITipoMovimientoJpaRepositorio extends JpaRepository<TipoMovimientoEntity, Integer> {

}
