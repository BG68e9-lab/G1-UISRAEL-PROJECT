package com.uisrael.drinkhouse.infraestructura.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoId;

public interface ISecuenciaCodigoJpaRepositorio extends JpaRepository<SecuenciaCodigoEntity, SecuenciaCodigoId> {

	Optional<SecuenciaCodigoEntity> findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(
			Integer negocioId, Integer tipoMovimientoId);

	/** Compatibilidad con tests legacy — busca por código de tipo de movimiento */
	Optional<SecuenciaCodigoEntity> findByTipoMovimiento_Codigo(String codigo);

	/** Alias para compatibilidad con tests legacy */
	default Optional<SecuenciaCodigoEntity> findByTipo(String tipo) {
		return findByTipoMovimiento_Codigo(tipo);
	}
}
