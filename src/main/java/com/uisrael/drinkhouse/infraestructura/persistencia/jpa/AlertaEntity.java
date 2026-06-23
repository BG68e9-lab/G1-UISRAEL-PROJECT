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
@Table(name = "alertas")
public class AlertaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "alerta_id")
	private Long alertaId;
	
	@Column(name = "tipo", nullable = false, length = 30)
	private String tipo;
	
	@Column(name = "mensaje", nullable = false, length = 255)
	private String mensaje;
	
	@Column(name = "leido", nullable = false)
	private Boolean leido;
	
	@Column(name = "creado_en", updatable = false, nullable = false)
	private OffsetDateTime creadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
		if (this.leido == null) {
			this.leido = false;
		}
	}
}