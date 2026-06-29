package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;

public interface IProveedorJpaRepositorio extends JpaRepository<ProveedorEntity, Integer> {

}
