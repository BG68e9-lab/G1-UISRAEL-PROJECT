package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "detalle_orden_compra")
public class DetalleOrdenCompraEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "detalle_oc_id")
	private Long detalleOcId;

	@ManyToOne
	@JoinColumn(name = "orden_compra_id", nullable = false)
	private OrdenCompraEntity ordenCompraId;

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private ProductoEntity fkProductoEntity;

	@Column(name = "descripcion_factura", length = 300)
	private String descripcionFactura;

	@Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 12, scale = 4)
	private BigDecimal precioUnitario;

	@Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
	private BigDecimal subtotal;

	@PrePersist
	@PreUpdate
	protected void calcularSubtotal() {
		if (this.cantidad != null && this.precioUnitario != null && this.subtotal == null) {
			this.subtotal = this.cantidad.multiply(this.precioUnitario);
		}
	}
}
