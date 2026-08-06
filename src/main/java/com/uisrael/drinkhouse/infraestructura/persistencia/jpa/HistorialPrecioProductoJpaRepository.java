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
public interface HistorialPrecioProductoJpaRepository extends JpaRepository<HistorialPrecioProductoEntity, Long> {

	/**
	 * Busca todo el historial de precios de un producto ordenado por fecha descendente
	 */
	List<HistorialPrecioProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de precios de un producto con paginación
	 */
	Page<HistorialPrecioProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);
	
	/**
	 * Busca el último cambio de precio de un producto
	 */
	HistorialPrecioProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);
	
	/**
	 * Busca historial de precios por origen del cambio
	 */
	List<HistorialPrecioProductoEntity> findByOrigenCambio(String origenCambio);
	
	/**
	 * Busca historial de precios por factura relacionada
	 */
	List<HistorialPrecioProductoEntity> findByFacturaRelacionada(String facturaRelacionada);
	
	/**
	 * Busca historial de precios en un rango de fechas
	 */
	@Query("SELECT h FROM HistorialPrecioProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio BETWEEN :fechaInicio AND :fechaFin " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialPrecioProductoEntity> findByProductoIdAndFechaCambioBetween(
		@Param("productoId") Long productoId,
		@Param("fechaInicio") OffsetDateTime fechaInicio,
		@Param("fechaFin") OffsetDateTime fechaFin
	);
	
	/**
	 * Busca el precio vigente en una fecha específica (último cambio antes o igual a esa fecha)
	 */
	@Query("SELECT h FROM HistorialPrecioProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialPrecioProductoEntity> findPrecioEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);
	
	/**
	 * Cuenta cambios de precio de un producto
	 */
	long countByProductoId(Long productoId);
	
	/**
	 * Busca historial por usuario modificador
	 */
	List<HistorialPrecioProductoEntity> findByUsuarioModificadorOrderByFechaCambioDesc(String usuarioModificador);
}
