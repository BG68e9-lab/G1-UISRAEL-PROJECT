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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lotes_producto")
public class LoteProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lote_id")
	private Long loteId;

	@ManyToOne
	@JoinColumn(name = "negocio_id", nullable = false)
	private NegocioEntity negocioId;

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private ProductoEntity productoId;

	@ManyToOne
	@JoinColumn(name = "orden_compra_id", nullable = false)
	private OrdenCompraEntity ordenCompraId;

	@ManyToOne
	@JoinColumn(name = "estado_respaldo_id", nullable = false)
	private EstadoRespaldoEntity estadoRespaldoId;

	@ManyToOne
	@JoinColumn(name = "registrado_por", nullable = false)
	private UsuarioEntity registradoPor;

	@Column(name = "codigo_entrada", nullable = false, length = 50)
	private String codigoEntrada;

	@Column(name = "cantidad_inicial", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidadInicial;

	@Column(name = "cantidad_disponible", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidadDisponible;

	@Column(name = "precio_costo", precision = 10, scale = 2)
	private BigDecimal precioCosto;

	@Column(name = "fecha_ingreso", nullable = false, updatable = false)
	private OffsetDateTime fechaIngreso;

	@Column(name = "fecha_vencimiento")
	private OffsetDateTime fechaVencimiento;

	@PrePersist
	protected void onCreate() {
		this.fechaIngreso = OffsetDateTime.now();
	}
}