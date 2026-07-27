package com.uisrael.drinkhouse.infraestructura.repositorio;

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
}
