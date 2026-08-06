package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AjusteInventarioAuditoriaEntity;

/**
 * Spring Data JPA repository for AjusteInventarioAuditoriaEntity.
 * Provides data access operations for audit records of inventory movements.
 * 
 * @see AjusteInventarioAuditoriaEntity
 * @see com.uisrael.drinkhouse.dominio.repositorios.IAjusteInventarioAuditoriaRepositorio
 */
public interface IAjusteInventarioAuditoriaJpaRepositorio extends JpaRepository<AjusteInventarioAuditoriaEntity, Long> {

	/**
	 * Finds audit record by movement ID.
	 * 
	 * @param movimientoId Movement identifier
	 * @return Optional containing the audit record if found
	 */
	@Query("SELECT a FROM AjusteInventarioAuditoriaEntity a WHERE a.movimientoId = :movimientoId")
	Optional<AjusteInventarioAuditoriaEntity> findByMovimientoId(@Param("movimientoId") Long movimientoId);
}
