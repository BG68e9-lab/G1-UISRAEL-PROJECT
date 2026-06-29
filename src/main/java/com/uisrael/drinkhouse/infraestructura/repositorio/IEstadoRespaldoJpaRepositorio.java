package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoRespaldoEntity;

public interface IEstadoRespaldoJpaRepositorio extends JpaRepository<EstadoRespaldoEntity, Integer> {

}
