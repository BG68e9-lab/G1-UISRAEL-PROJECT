package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;

import jakarta.persistence.LockModeType;

public interface IProductoJpaRepositorio extends JpaRepository<ProductoEntity, Long>,
		JpaSpecificationExecutor<ProductoEntity> {

	boolean existsByNombre(String nombre);

	@Query("SELECT p FROM ProductoEntity p " +
		   "LEFT JOIN FETCH p.fkCategoriaEntity " +
		   "LEFT JOIN FETCH p.fkTipoProductoEntity " +
		   "WHERE p.productoId = :id")
	Optional<ProductoEntity> findByIdWithRelations(@Param("id") Long id);

	@Query("SELECT p FROM ProductoEntity p " +
		   "LEFT JOIN FETCH p.fkCategoriaEntity " +
		   "LEFT JOIN FETCH p.fkTipoProductoEntity")
	List<ProductoEntity> findAllWithRelations();

	/**
	 * Locks product row for update to prevent concurrent modifications.
	 * Uses SELECT FOR UPDATE to acquire pessimistic write lock.
	 * 
	 * @param productoId Product identifier
	 * @return Optional containing the locked product entity, or empty if not found
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT p FROM ProductoEntity p WHERE p.productoId = :productoId")
	Optional<ProductoEntity> findByIdForUpdate(@Param("productoId") Long productoId);
}
