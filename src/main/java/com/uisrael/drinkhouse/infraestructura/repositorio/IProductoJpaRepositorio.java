package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;

public interface IProductoJpaRepositorio extends JpaRepository<ProductoEntity, Long>,
		JpaSpecificationExecutor<ProductoEntity> {

	boolean existsByNombre(String nombre);
}
