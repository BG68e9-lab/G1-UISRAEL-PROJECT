package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface INotaVentaJpaRepositorio extends JpaRepository<NotaVentaEntity, Long> {

List<NotaVentaEntity> findAllByOrderByCreadoEnDesc();
}
