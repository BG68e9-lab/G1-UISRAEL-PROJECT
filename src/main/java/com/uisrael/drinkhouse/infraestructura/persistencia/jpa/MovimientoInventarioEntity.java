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

	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;

	@ManyToOne
	@JoinColumn(name = "tipo_movimiento_id", nullable = false)
	private TipoMovimientoEntity tipoMovimientoId;
	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private ProductoEntity productoId;

	@ManyToOne
	@JoinColumn(name = "lote_id", nullable = false)
	private LoteProductoEntity loteId;

	@ManyToOne
	@JoinColumn(name = "usuario_id", nullable = false)
	private UsuarioEntity usuarioId;

	@Column(name = "codigo_movimiento", nullable = false, length = 50)
	private String codigoMovimiento;

	@Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidad;

	@Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
	private BigDecimal precioUnitario;

}