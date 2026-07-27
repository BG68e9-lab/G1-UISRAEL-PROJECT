package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;

public interface ILoteProductoJpaRepositorio extends JpaRepository<LoteProductoEntity, Long> {

	List<LoteProductoEntity> findByFkProductoEntityProductoIdOrderByFechaIngresoAsc(Long productoId);

	@Query("SELECT l FROM LoteProductoEntity l WHERE l.fechaVencimiento <= :limite AND l.cantidadDisponible > 0")
	List<LoteProductoEntity> findProximosAVencer(@Param("limite") LocalDate limite);
}
