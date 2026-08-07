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

List<HistorialPrecioProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);

Page<HistorialPrecioProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);

HistorialPrecioProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);

List<HistorialPrecioProductoEntity> findByOrigenCambio(String origenCambio);

List<HistorialPrecioProductoEntity> findByFacturaRelacionada(String facturaRelacionada);

@Query("SELECT h FROM HistorialPrecioProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio BETWEEN :fechaInicio AND :fechaFin " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialPrecioProductoEntity> findByProductoIdAndFechaCambioBetween(
		@Param("productoId") Long productoId,
		@Param("fechaInicio") OffsetDateTime fechaInicio,
		@Param("fechaFin") OffsetDateTime fechaFin
	);

@Query("SELECT h FROM HistorialPrecioProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialPrecioProductoEntity> findPrecioEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);

long countByProductoId(Long productoId);

List<HistorialPrecioProductoEntity> findByUsuarioModificadorOrderByFechaCambioDesc(String usuarioModificador);
}
