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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estados_oc")
public class EstadoOcEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "estado_oc_id")
	private Integer estadoOcId;
	@Column(name = "codigo", nullable = false, length = 30)
	private String codigo;
	@Column(name = "etiqueta", nullable = false, length = 60)
	private String etiqueta;

}
