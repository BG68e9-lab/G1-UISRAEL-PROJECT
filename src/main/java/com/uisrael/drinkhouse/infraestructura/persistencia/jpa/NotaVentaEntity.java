package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notas_venta")
public class NotaVentaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "nota_id")
	private Long notaId;

	@Column(name = "fecha", nullable = false, length = 50)
	private String fecha;

	@Column(name = "nombre_cliente", length = 255)
	private String nombreCliente;

	@Column(name = "producto_vendido", nullable = false, columnDefinition = "TEXT")
	private String productoVendido;

	@Column(name = "precio_unitario", length = 50)
	private String precioUnitario;

	@Column(name = "total", length = 50)
	private String total;

	@Column(name = "observaciones", columnDefinition = "TEXT")
	private String observaciones;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}
