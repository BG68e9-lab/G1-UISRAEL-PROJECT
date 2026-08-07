package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Data
@Entity
@Table(name = "secuencias_codigo")
@IdClass(SecuenciaCodigoId.class)
public class SecuenciaCodigoEntity {

	@Id
	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocio;

	@Id
	@ManyToOne
	@JoinColumn(name = "tipo_movimiento_id", nullable = false)
	private TipoMovimientoEntity tipoMovimiento;

	@Column(name = "ultimo_numero", nullable = false)
	private Long ultimoNumero;

	@Version
	private Long version;
}
