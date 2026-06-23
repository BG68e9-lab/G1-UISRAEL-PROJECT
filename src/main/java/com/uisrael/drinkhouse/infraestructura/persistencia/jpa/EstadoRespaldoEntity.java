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
@Table (name= "estados_respaldo")
public class EstadoRespaldoEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "estado_respaldo_id")
	private Integer estadoRespaldoId;
	@Column(name = "estado_respaldo_id", nullable = false)
	private String codigo;
	@Column(name = "etiqueta", nullable = false)
	private String etiqueta;
	@Column(name = "icono", nullable = false)
	private String icono;


}
