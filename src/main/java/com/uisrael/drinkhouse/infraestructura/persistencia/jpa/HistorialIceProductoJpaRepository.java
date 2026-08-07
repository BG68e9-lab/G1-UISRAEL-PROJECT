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

List<HistorialIceProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId);

Page<HistorialIceProductoEntity> findByProductoIdOrderByFechaCambioDesc(Long productoId, Pageable pageable);

HistorialIceProductoEntity findTopByProductoIdOrderByFechaCambioDesc(Long productoId);

List<HistorialIceProductoEntity> findByOrigenCambioOrderByFechaCambioDesc(String origenCambio);

List<HistorialIceProductoEntity> findByGrupoIceOrderByFechaCambioDesc(String grupoIce);

List<HistorialIceProductoEntity> findByEsMonofasicoOrderByFechaCambioDesc(Boolean esMonofasico);

List<HistorialIceProductoEntity> findByResolucionSriOrderByFechaCambioDesc(String resolucionSri);

@Query("SELECT h FROM HistorialIceProductoEntity h WHERE h.productoId = :productoId " +
	       "AND h.fechaCambio <= :fecha " +
	       "ORDER BY h.fechaCambio DESC")
	List<HistorialIceProductoEntity> findIceEnFecha(
		@Param("productoId") Long productoId,
		@Param("fecha") OffsetDateTime fecha,
		Pageable pageable
	);

long countByProductoId(Long productoId);

@Query("SELECT DISTINCT h FROM HistorialIceProductoEntity h " +
	       "WHERE h.grupoIce = :grupoIce AND h.aplicaIceNuevo = true " +
	       "AND h.fechaCambio = (SELECT MAX(h2.fechaCambio) FROM HistorialIceProductoEntity h2 " +
	       "WHERE h2.productoId = h.productoId)")
	List<HistorialIceProductoEntity> findProductosActivosConIcePorGrupo(@Param("grupoIce") String grupoIce);
}
