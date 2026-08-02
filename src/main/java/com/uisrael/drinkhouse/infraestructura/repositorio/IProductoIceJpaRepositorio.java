package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoIceEntity;

public interface IProductoIceJpaRepositorio extends JpaRepository<ProductoIceEntity, Long> {

	Optional<ProductoIceEntity> findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(
			Long productoId);

	List<ProductoIceEntity> findByFkProductoEntity_ProductoIdOrderByVigenteDesdeDesc(Long productoId);
}
