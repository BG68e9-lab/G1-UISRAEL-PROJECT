package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "detalles_orden_compra")
public class DetalleOrdenCompraEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "detalle_orden_compra_id")
	private Long detalleOrdenCompraId;

	@ManyToOne
	@JoinColumn(name = "orden_compra_id", nullable = false)
	private OrdenCompraEntity fkOrdenCompraEntity;

	@ManyToOne
	@JoinColumn(name = "producto_id")
	private ProductoEntity fkProductoEntity;

	@Column(name = "cantidad", nullable = false)
	private Integer cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioUnitario;

	@Column(name = "subtotal", precision = 12, scale = 2)
	private BigDecimal subtotal;

	@Column(name = "observaciones", length = 200)
	private String observaciones;
}
