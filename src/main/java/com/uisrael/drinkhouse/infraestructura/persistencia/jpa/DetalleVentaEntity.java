package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "detalles_venta")
public class DetalleVentaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "detalle_venta_id")
	private Long detalleVentaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "venta_id", nullable = false)
	@JsonIgnore
	private VentaEntity fkVentaEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "producto_id", nullable = false)
	@JsonIgnore
	private ProductoEntity fkProductoEntity;

	@Column(name = "cantidad", nullable = false)
	private Integer cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	@Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
	private BigDecimal subtotal;
}
