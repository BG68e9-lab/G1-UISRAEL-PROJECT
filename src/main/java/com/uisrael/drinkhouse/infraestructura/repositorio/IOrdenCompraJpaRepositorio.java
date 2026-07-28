package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;

public interface IOrdenCompraJpaRepositorio extends JpaRepository<OrdenCompraEntity, Long> {

	Optional<OrdenCompraEntity> findByCodigoReferencia(String codigoReferencia);

}
