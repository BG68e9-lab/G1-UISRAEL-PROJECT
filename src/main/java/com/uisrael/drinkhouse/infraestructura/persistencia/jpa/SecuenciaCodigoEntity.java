package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "secuencias_codigo")
@IdClass(SecuenciaCodigoEntity.SecuenciaCodigoId.class)
public class SecuenciaCodigoEntity {

	@Id
	@Column(name = "negocio_id")
	private Integer negocioId;

	@Id
	@Column(name = "tipo_movimiento_id")
	private Integer tipoMovimientoId;

	@ManyToOne
	@JoinColumn(name = "negocio_id", insertable = false, updatable = false)
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "tipo_movimiento_id", insertable = false, updatable = false)
	private TipoMovimientoEntity fkTipoMovimientoEntity;

	@Column(name = "ultimo_numero", nullable = false)
	private Long ultimoNumero;

	@Data
	public static class SecuenciaCodigoId implements Serializable {
		private Integer negocioId;
		private Integer tipoMovimientoId;
	}
}
