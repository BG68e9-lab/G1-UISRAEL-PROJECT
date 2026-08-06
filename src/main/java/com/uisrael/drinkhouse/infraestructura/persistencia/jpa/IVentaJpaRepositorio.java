package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVentaJpaRepositorio extends JpaRepository<VentaEntity, Long> {
}
