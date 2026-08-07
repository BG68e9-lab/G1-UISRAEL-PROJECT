package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoId;

public interface ISecuenciaCodigoJpaRepositorio extends JpaRepository<SecuenciaCodigoEntity, SecuenciaCodigoId> {

	Optional<SecuenciaCodigoEntity> findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(
			Integer negocioId, Integer tipoMovimientoId);

	List<SecuenciaCodigoEntity> findByNegocio_NegocioId(Integer negocioId);

Optional<SecuenciaCodigoEntity> findByTipoMovimiento_Codigo(String codigo);

default Optional<SecuenciaCodigoEntity> findByTipo(String tipo) {
		return findByTipoMovimiento_Codigo(tipo);
	}
}
