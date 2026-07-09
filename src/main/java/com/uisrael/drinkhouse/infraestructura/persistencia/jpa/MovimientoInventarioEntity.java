package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "movimientos_inventario")
public class MovimientoInventarioEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "movimiento_id")
	private Long movimientoId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "tipo_movimiento_id")
	private TipoMovimientoEntity fkTipoMovimientoEntity;

	@ManyToOne
	@JoinColumn(name = "producto_id")
	private ProductoEntity fkProductoEntity;

	@ManyToOne
	@JoinColumn(name = "lote_id")
	private LoteProductoEntity fkLoteEntity;

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private UsuarioEntity fkUsuarioEntity;

	@Column(name = "codigo_movimiento", nullable = false, length = 15)
	private String codigoMovimiento;

	@Column(name = "cantidad", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidad;

	@Column(name = "precio_unitario", precision = 12, scale = 4)
	private BigDecimal precioUnitario;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}
