package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoPrecioHistoricoEntity;

public interface IProductoPrecioHistoricoJpaRepositorio extends JpaRepository<ProductoPrecioHistoricoEntity, Long> {

	Optional<ProductoPrecioHistoricoEntity> findFirstByFkProductoEntity_ProductoIdAndVigenteHastaIsNullOrderByVigenteDesdeDesc(
			Long productoId);

	List<ProductoPrecioHistoricoEntity> findByFkProductoEntity_ProductoIdOrderByVigenteDesdeDesc(Long productoId);
}
