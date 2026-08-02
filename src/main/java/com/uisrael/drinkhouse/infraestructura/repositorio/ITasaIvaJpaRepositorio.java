package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TasaIvaEntity;

public interface ITasaIvaJpaRepositorio extends JpaRepository<TasaIvaEntity, Long> {

	Optional<TasaIvaEntity> findFirstByVigenteHastaIsNullOrderByVigenteDesdeDesc();

	List<TasaIvaEntity> findAllByOrderByVigenteDesdeDesc();
}
