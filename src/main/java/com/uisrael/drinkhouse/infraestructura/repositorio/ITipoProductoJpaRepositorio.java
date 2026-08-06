package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoProductoEntity;

public interface ITipoProductoJpaRepositorio extends JpaRepository<TipoProductoEntity, Long> {

	@Query("SELECT t FROM TipoProductoEntity t WHERE t.fkCategoriaEntity.categoriaId = :categoriaId")
	List<TipoProductoEntity> findByCategoriaId(@Param("categoriaId") Long categoriaId);

	@Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TipoProductoEntity t " +
			"WHERE t.nombre = :nombre AND t.fkCategoriaEntity.categoriaId = :categoriaId")
	boolean existsByNombreAndCategoriaId(@Param("nombre") String nombre, @Param("categoriaId") Long categoriaId);

	@Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductoEntity p " +
			"WHERE p.fkTipoProductoEntity.tipoProductoId = :tipoProductoId")
	boolean hasProductosAsociados(@Param("tipoProductoId") Long tipoProductoId);

}
