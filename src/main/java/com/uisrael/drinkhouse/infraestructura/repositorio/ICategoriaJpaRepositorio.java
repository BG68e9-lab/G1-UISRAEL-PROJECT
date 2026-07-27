package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;

public interface ICategoriaJpaRepositorio extends JpaRepository<CategoriaEntity, Long> {

	boolean existsByNombre(String nombre);

	@Query("SELECT COUNT(p) > 0 FROM ProductoEntity p WHERE p.fkCategoriaEntity.categoriaId = :id")
	boolean tieneProductos(@Param("id") Long id);

}
