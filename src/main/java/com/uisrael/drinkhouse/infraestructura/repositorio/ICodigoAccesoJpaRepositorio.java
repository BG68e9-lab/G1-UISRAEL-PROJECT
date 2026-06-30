package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;

public interface ICodigoAccesoJpaRepositorio extends JpaRepository<CodigoAccesoEntity, UUID> {

}
