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

List<HistorialIvaProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);

Page<HistorialIvaProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);

HistorialIvaProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);

List<HistorialIvaProductoEntity> findByOrigenCambioOrderByFechaCambioDesc(String origenCambio);

List<HistorialIvaProductoEntity> findByResolucionSriOrderByFechaCambioDesc(String resolucionSri);

@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.origenCambio = 'CAMBIO_LEY' " +
	       "AND h.resolucionSri = :resolucion " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIvaProductoEntity> findByReformaTributaria(@Param("resolucion") String resolucion);

@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.fechaVigencia <= :fecha " +
	       "ORDER BY h.fechaVigencia DESC")
	List<HistorialIvaProductoEntity> findVigentesEnFecha(@Param("fecha") LocalDate fecha);

@Query("SELECT h FROM HistorialIvaProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIvaProductoEntity> findIvaEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);

long countByProductoId(Long productoId);
}
