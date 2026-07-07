package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "secuencias_codigo")
public class SecuenciaCodigoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "secuencia_id")
	private Integer secuenciaId;

	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;
	@ManyToOne
	@JoinColumn(name = "tipo_movimiento_id", nullable = false)
	private TipoMovimientoEntity tipoMovimientoId;

	@Column(name = "ultimo_numero", nullable = false)
	private Integer ultimoNumero;

}