package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;

public interface IUsuarioJpaRepositorio extends JpaRepository<UsuarioEntity, UUID> {

}
