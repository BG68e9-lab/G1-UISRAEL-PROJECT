package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.DetalleOrdenCompraEntity;

public interface IDetalleOrdenCompraJpaRepositorio extends JpaRepository<DetalleOrdenCompraEntity, Long> {

	List<DetalleOrdenCompraEntity> findByOrdenCompraIdOrdenCompraId(Long ordenCompraId);

	void deleteByOrdenCompraIdOrdenCompraId(Long ordenCompraId);
}
