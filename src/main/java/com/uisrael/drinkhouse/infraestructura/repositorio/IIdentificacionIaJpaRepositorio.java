package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.IdentificacionIaEntity;

public interface IIdentificacionIaJpaRepositorio extends JpaRepository<IdentificacionIaEntity, Long> {

    @Query("SELECT i FROM IdentificacionIaEntity i " +
           "WHERE (:productoId IS NULL OR i.producto.productoId = :productoId) " +
           "AND (:desde IS NULL OR i.creadoEn >= :desde) " +
           "AND (:hasta IS NULL OR i.creadoEn <= :hasta) " +
           "ORDER BY i.creadoEn DESC")
    List<IdentificacionIaEntity> buscarConFiltros(
            @Param("productoId") Long productoId,
            @Param("desde") OffsetDateTime desde,
            @Param("hasta") OffsetDateTime hasta);

    /**
     * Cuenta las identificaciones de un negocio en un período específico (mes).
     *
     * @param negocioId ID del negocio
     * @param periodo   primer día del mes (ej: 2026-07-01)
     * @return cantidad de identificaciones realizadas en ese mes
     */
    @Query("SELECT COUNT(i) FROM IdentificacionIaEntity i " +
           "WHERE i.negocio.negocioId = :negocioId " +
           "AND FUNCTION('DATE_TRUNC', 'month', i.creadoEn) = " +
           "CAST(:periodo AS timestamp)")
    int contarPorNegocioYPeriodo(
            @Param("negocioId") Integer negocioId,
            @Param("periodo") LocalDate periodo);
}
