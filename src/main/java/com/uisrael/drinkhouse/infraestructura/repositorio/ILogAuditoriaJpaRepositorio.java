package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LogAuditoriaEntity;

public interface ILogAuditoriaJpaRepositorio extends JpaRepository<LogAuditoriaEntity, Long> {

}
