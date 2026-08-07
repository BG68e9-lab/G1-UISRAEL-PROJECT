package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;

public interface IMovimientoInventarioJpaRepositorio extends JpaRepository<MovimientoInventarioEntity, Long> {

	@Query("SELECT m FROM MovimientoInventarioEntity m WHERE "
			+ "m.fkProductoEntity.productoId = :productoId AND "
			+ "(:tipo IS NULL OR m.fkTipoMovimientoEntity.codigo = :tipo) AND "
			+ "(:desde IS NULL OR m.creadoEn >= :desde) AND "
			+ "(:hasta IS NULL OR m.creadoEn <= :hasta) "
			+ "ORDER BY m.creadoEn DESC")
	List<MovimientoInventarioEntity> buscarConFiltros(
			@Param("productoId") Long productoId,
			@Param("tipo") String tipo,
			@Param("desde") OffsetDateTime desde,
			@Param("hasta") OffsetDateTime hasta);

@Query("SELECT m FROM MovimientoInventarioEntity m ORDER BY m.creadoEn DESC")
	List<MovimientoInventarioEntity> findAllOrderByCreadoEnDesc();

@Query("SELECT m FROM MovimientoInventarioEntity m WHERE m.fkTipoMovimientoEntity.codigo = :codigoTipo ORDER BY m.creadoEn DESC")
	List<MovimientoInventarioEntity> findByTipoMovimiento(@Param("codigoTipo") String codigoTipo);

@Query("SELECT m FROM MovimientoInventarioEntity m WHERE m.fkLoteEntity.loteId = :loteId ORDER BY m.creadoEn DESC")
	List<MovimientoInventarioEntity> findByLote(@Param("loteId") Long loteId);

@Query("SELECT m FROM MovimientoInventarioEntity m WHERE m.ventaId = :ventaId")
	List<MovimientoInventarioEntity> findByVentaId(@Param("ventaId") Long ventaId);
}
