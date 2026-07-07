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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tipos_movimiento")
public class TipoMovimientoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tipo_movimiento_id")
	private Integer tipoMovimientoId;

	@Column(name = "codigo", nullable = false, length = 50)
	private String codigo;

	@Column(name = "prefijo_codigo", nullable = false, length = 10)
	private String prefijoCodigo;

	@Column(name = "descripcion", length = 255)
	private String descripcion;

}