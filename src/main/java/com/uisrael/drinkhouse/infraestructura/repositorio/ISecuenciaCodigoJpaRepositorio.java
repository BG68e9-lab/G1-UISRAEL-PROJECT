package com.uisrael.drinkhouse.infraestructura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity.SecuenciaCodigoId;

public interface ISecuenciaCodigoJpaRepositorio extends JpaRepository<SecuenciaCodigoEntity, SecuenciaCodigoId> {

}
