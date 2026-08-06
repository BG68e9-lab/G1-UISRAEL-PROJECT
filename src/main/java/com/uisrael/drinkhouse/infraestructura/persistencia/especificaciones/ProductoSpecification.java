package com.uisrael.drinkhouse.infraestructura.persistencia.especificaciones;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;

import jakarta.persistence.criteria.Predicate;

public class ProductoSpecification {

	private ProductoSpecification() {}

	public static Specification<ProductoEntity> conFiltros(
			String nombre, String marca, Long tipoProductoId, Long categoriaId) {

		return (root, query, cb) -> {
			if (query != null) {
				query.distinct(true);
				root.fetch("fkCategoriaEntity", jakarta.persistence.criteria.JoinType.LEFT);
				root.fetch("fkTipoProductoEntity", jakarta.persistence.criteria.JoinType.LEFT);
			}
			
			List<Predicate> predicados = new ArrayList<>();

			if (nombre != null && !nombre.isBlank()) {
				predicados.add(cb.like(
						cb.lower(root.get("nombre")),
						"%" + nombre.toLowerCase() + "%"));
			}
			if (marca != null && !marca.isBlank()) {
				predicados.add(cb.like(
						cb.lower(root.get("marca")),
						"%" + marca.toLowerCase() + "%"));
			}
			if (tipoProductoId != null) {
				predicados.add(cb.equal(
						root.get("fkTipoProductoEntity").get("tipoProductoId"),
						tipoProductoId));
			}
			if (categoriaId != null) {
				predicados.add(cb.equal(
						root.get("fkCategoriaEntity").get("categoriaId"),
						categoriaId));
			}

			if (predicados.isEmpty()) {
				return cb.conjunction();
			}
			
			return cb.and(predicados.toArray(new Predicate[0]));
		};
	}
}
