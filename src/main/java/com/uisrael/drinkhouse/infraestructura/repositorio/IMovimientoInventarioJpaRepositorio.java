package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;

public interface IMovimientoInventarioJpaRepositorio extends JpaRepository<MovimientoInventarioEntity, Integer> {

}
