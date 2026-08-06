package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialIceProductoJpaRepository extends JpaRepository<HistorialIceProductoEntity, Long> {

	/**
	 * Busca todo el historial de ICE de un producto ordenado por fecha descendente
	 */
	List<HistorialIceProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de ICE de un producto con paginación
	 */
	Page<HistorialIceProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);
	
	/**
	 * Busca el último cambio de ICE de un producto
	 */
	HistorialIceProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de ICE por origen del cambio
	 */
	List<HistorialIceProductoEntity> findByOrigenCambioOrderByFechaCambioDesc(String origenCambio);
	
	/**
	 * Busca productos por grupo de ICE
	 */
	List<HistorialIceProductoEntity> findByGrupoIceOrderByFechaCambioDesc(String grupoIce);
	
	/**
	 * Busca productos con ICE monofásico
	 */
	List<HistorialIceProductoEntity> findByEsMonofasicoOrderByFechaCambioDesc(Boolean esMonofasico);
	
	/**
	 * Busca cambios de ICE por resolución del SRI
	 */
	List<HistorialIceProductoEntity> findByResolucionSriOrderByFechaCambioDesc(String resolucionSri);
	
	/**
	 * Busca el ICE vigente para un producto en una fecha específica
	 */
	@Query("SELECT h FROM HistorialIceProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIceProductoEntity> findIceEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);
	
	/**
	 * Cuenta cambios de ICE de un producto
	 */
	long countByProductoId(Long productoId);
	
	/**
	 * Busca todos los productos que actualmente aplican ICE por grupo
	 */
	@Query("SELECT DISTINCT h FROM HistorialIceProductoEntity h " +
	       "WHERE h.grupoIce = :grupoIce AND h.aplicaIceNuevo = true " +
	       "AND h.fechaCambio = (SELECT MAX(h2.fechaCambio) FROM HistorialIceProductoEntity h2 " +
	       "WHERE h2.productoId = h.productoId)")
	List<HistorialIceProductoEntity> findProductosActivosConIcePorGrupo(@Param("grupoIce") String grupoIce);
}
