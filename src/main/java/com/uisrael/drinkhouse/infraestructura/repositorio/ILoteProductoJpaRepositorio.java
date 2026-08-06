package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;

public interface ILoteProductoJpaRepositorio extends JpaRepository<LoteProductoEntity, Long> {

	List<LoteProductoEntity> findByFkProductoEntityProductoIdOrderByFechaIngresoAsc(Long productoId);

	@Query("SELECT l FROM LoteProductoEntity l WHERE l.fechaVencimiento IS NOT NULL AND l.fechaVencimiento <= :limite AND l.cantidadDisponible > 0")
	List<LoteProductoEntity> findProximosAVencer(@Param("limite") LocalDate limite);

	@Query("SELECT l FROM LoteProductoEntity l LEFT JOIN FETCH l.fkProductoEntity ORDER BY l.fechaIngreso DESC")
	List<LoteProductoEntity> findAllWithProducto();

	@Query(value = "SELECT l FROM LoteProductoEntity l LEFT JOIN FETCH l.fkProductoEntity",
		   countQuery = "SELECT COUNT(l) FROM LoteProductoEntity l")
	org.springframework.data.domain.Page<LoteProductoEntity> findAllWithProductoPaginado(org.springframework.data.domain.Pageable pageable);
}
