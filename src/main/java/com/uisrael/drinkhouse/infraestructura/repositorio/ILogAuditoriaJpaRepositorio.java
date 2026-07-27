package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LogAuditoriaEntity;

public interface ILogAuditoriaJpaRepositorio extends JpaRepository<LogAuditoriaEntity, Long> {

	@Query("SELECT l FROM LogAuditoriaEntity l WHERE "
			+ "(:entidad IS NULL OR l.entidad = :entidad) AND "
			+ "(:accion IS NULL OR l.accion = :accion) AND "
			+ "(:desde IS NULL OR l.creadoEn >= :desde) AND "
			+ "(:hasta IS NULL OR l.creadoEn <= :hasta)")
	List<LogAuditoriaEntity> buscarConFiltros(
			@Param("entidad") String entidad,
			@Param("accion") String accion,
			@Param("desde") OffsetDateTime desde,
			@Param("hasta") OffsetDateTime hasta);

	List<LogAuditoriaEntity> findByEntidadId(String entidadId);

}
