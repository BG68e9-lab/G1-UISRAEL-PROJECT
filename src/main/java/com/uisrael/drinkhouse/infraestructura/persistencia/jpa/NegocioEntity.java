package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.LocalDateTime;

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
@Table(name = "negocios")
public class NegocioEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "negocio_id")
	private Integer negocioId;
	@Column(name = "nombre", nullable = false, length = 120)
	private String nombre;
	@Column(name = "ruc", nullable = false, length = 13, unique = true)
	private String ruc;
	@Column(name = "activo", nullable = false)
	private Boolean activo;
	@Column(name = "creado_en", updatable = false)
	private LocalDateTime creadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.creadoEn = LocalDateTime.now();
	}

}
