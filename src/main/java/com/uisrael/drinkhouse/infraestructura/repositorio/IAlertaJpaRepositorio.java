package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AlertaEntity;

public interface IAlertaJpaRepositorio extends JpaRepository<AlertaEntity, Long> {

	List<AlertaEntity> findByTipoAlertaAndAtendidaOrderByCreadoEnDesc(String tipo, Boolean atendida);

	List<AlertaEntity> findByTipoAlertaOrderByCreadoEnDesc(String tipo);

	List<AlertaEntity> findByAtendidaOrderByCreadoEnDesc(Boolean atendida);

	List<AlertaEntity> findAllByOrderByCreadoEnDesc();

	long countByAtendidaFalse();
}
