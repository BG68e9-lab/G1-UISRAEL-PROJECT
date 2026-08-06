package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;

public interface IProveedorJpaRepositorio extends JpaRepository<ProveedorEntity, Long> {

	boolean existsByRuc(String ruc);
	
	Optional<ProveedorEntity> findByRuc(String ruc);

	@Query("SELECT COUNT(o) > 0 FROM OrdenCompraEntity o WHERE o.fkProveedorEntity.proveedorId = :proveedorId")
	boolean tieneOrdenes(@Param("proveedorId") Long proveedorId);
}
