package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;

public interface ILoteProductoJpaRepositorio extends JpaRepository<LoteProductoEntity, Integer> {

}
