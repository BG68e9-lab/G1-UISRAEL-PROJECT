package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;

public interface IOrdenCompraJpaRepositorio extends JpaRepository<OrdenCompraEntity, Long> {

	@Query(value = "SELECT o.* FROM ordenes_compra o "
			+ "JOIN estados_oc e ON e.estado_oc_id = o.estado_oc_id "
			+ "WHERE (?1 IS NULL OR e.codigo = ?1) "
			+ "AND (CAST(?2 AS timestamptz) IS NULL OR o.creado_en >= CAST(?2 AS timestamptz)) "
			+ "AND (CAST(?3 AS timestamptz) IS NULL OR o.creado_en <= CAST(?3 AS timestamptz))",
			nativeQuery = true)
	List<OrdenCompraEntity> buscarConFiltros(
			@Param("estado") String estado,
			@Param("desde") OffsetDateTime desde,
			@Param("hasta") OffsetDateTime hasta);
}
