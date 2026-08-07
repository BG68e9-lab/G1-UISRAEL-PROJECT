package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;

public interface IAjusteInventarioAuditoriaJpaRepositorio extends JpaRepository<AjusteInventarioAuditoriaEntity, Long> {

@Query("SELECT a FROM AjusteInventarioAuditoriaEntity a WHERE a.movimientoId = :movimientoId")
	Optional<AjusteInventarioAuditoriaEntity> findByMovimientoId(@Param("movimientoId") Long movimientoId);
}
