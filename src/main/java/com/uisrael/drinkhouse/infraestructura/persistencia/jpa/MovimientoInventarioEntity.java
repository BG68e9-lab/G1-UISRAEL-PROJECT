package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.UUID;

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
@Entity
@Table(name = "movimientos_inventario")
@AllArgsConstructor
@NoArgsConstructor
public class MovimientoInventarioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "movimiento_id")
	private Long movimientoId; 

	@Column(name = "negocio_id", nullable = false)
	private Integer negocioId; 

	@Column(name = "tipo_movimiento_id", nullable = false)
	private Integer tipoMovimientoId; 

	@Column(name = "producto_id", nullable = false)
	private Long productoId;

	@Column(name = "lote_id", nullable = false)
	private Long loteId; 

	@Column(name = "codigo_movimiento", nullable = false, length = 50)
	private String codigoMovimiento; 

	@Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidad; 

	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

	@Column(name = "usuario_id", nullable = false)
	private UUID usuarioId; 
}
