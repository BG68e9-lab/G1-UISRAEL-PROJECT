package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialIvaProductoJpaRepository extends JpaRepository<HistorialIvaProductoEntity, Long> {

	/**
	 * Busca todo el historial de IVA de un producto ordenado por fecha descendente
	 */
	List<HistorialIvaProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de IVA de un producto con paginación
	 */
	Page<HistorialIvaProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);
	
	/**
	 * Busca el último cambio de IVA de un producto
	 */
	HistorialIvaProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de IVA por origen del cambio
	 */
	List<HistorialIvaProductoEntity> findByOrigenCambioOrderByFechaCambioDesc(String origenCambio);
	
	/**
	 * Busca cambios de IVA por resolución del SRI
	 */
	List<HistorialIvaProductoEntity> findByResolucionSriOrderByFechaCambioDesc(String resolucionSri);
	
	/**
	 * Busca productos afectados por una reforma tributaria específica
	 */
	@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.origenCambio = 'CAMBIO_LEY' " +
	       "AND h.resolucionSri = :resolucion " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIvaProductoEntity> findByReformaTributaria(@Param("resolucion") String resolucion);
	
	/**
	 * Busca cambios de IVA vigentes en una fecha específica
	 */
	@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.fechaVigencia <= :fecha " +
	       "ORDER BY h.fechaVigencia DESC")
	List<HistorialIvaProductoEntity> findVigentesEnFecha(@Param("fecha") LocalDate fecha);
	
	/**
	 * Busca el IVA vigente para un producto en una fecha específica
	 */
	@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIvaProductoEntity> findIvaEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);
	
	/**
	 * Cuenta cambios de IVA de un producto
	 */
	long countByProductoId(Long productoId);
}
