package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consumos_ia_mensual")
public class ConsumoIaMensualEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "consumo_ia_mensual_id")
	private Long consumoIaMensualId;
	
	@Column(name = "mes_anio", nullable = false, length = 7)
	private String mesAnio;
	
	@Column(name = "tokens_consumidos", nullable = false)
	private Long tokensConsumidos;
	
	@Column(name = "costo_estimado", nullable = false)
	private Double costoEstimado;
	
	@Column(name = "actualizado_en", nullable = false)
	private OffsetDateTime actualizadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.actualizadoEn = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.actualizadoEn = OffsetDateTime.now();
	}
}