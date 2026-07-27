package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ConsumoIaMensualEntity;

public interface IConsumoIaMensualJpaRepositorio extends JpaRepository<ConsumoIaMensualEntity, Long> {

    Optional<ConsumoIaMensualEntity> findByNegocio_NegocioIdAndPeriodo(Integer negocioId, LocalDate periodo);
}
