package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="secuencias_codigo")
public class SecuenciaCodigoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "negocio_id")
	private Integer negocioId;
	@Column(name= "tipo_movimiento_id", nullable = false)
	private Integer tipoMovimientoId;
	@Column(name= "ultimo_numero", nullable = false)
	private Integer ultimoNumero;
}
