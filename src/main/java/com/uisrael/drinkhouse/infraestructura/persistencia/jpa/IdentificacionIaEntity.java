package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "identificaciones_ia")
public class IdentificacionIaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "identificacion_ia_id")
	private Long identificacionIaId;
	
	@Column(name = "nombre_modelo", nullable = false, length = 80)
	private String nombreModelo;
	
	@Column(name = "probabilidad", nullable = false)
	private Double probabilidad;
	
	@Column(name = "resultado", nullable = false, length = 255)
	private String resultado;
	
	@Column(name = "creado_en", updatable = false, nullable = false)
	private OffsetDateTime creadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}