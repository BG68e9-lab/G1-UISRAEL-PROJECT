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
@Table(name = "ordenes_compra")
public class OrdenCompraEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orden_compra_id")
	private Long ordenCompraId;
	
	@Column(name = "codigo_referencia", nullable = false, unique = true, length = 50)
	private String codigoReferencia;
	
	@Column(name = "estado", nullable = false, length = 30)
	private String estado;
	
	@Column(name = "total", nullable = false)
	private Double total;
	
	@Column(name = "creado_en", updatable = false, nullable = false)
	private OffsetDateTime creadoEn;
	
	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}