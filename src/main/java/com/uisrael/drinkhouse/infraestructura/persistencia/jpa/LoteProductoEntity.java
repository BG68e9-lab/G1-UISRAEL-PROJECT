package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "lotes_producto")
public class LoteProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lote_id")
	private Long loteId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "producto_id")
	private ProductoEntity fkProductoEntity;

	@ManyToOne
	@JoinColumn(name = "orden_compra_id")
	private OrdenCompraEntity fkOrdenCompraEntity;

	@ManyToOne
	@JoinColumn(name = "estado_respaldo_id")
	private EstadoRespaldoEntity fkEstadoRespaldoEntity;

	@ManyToOne
	@JoinColumn(name = "registrado_por")
	private UsuarioEntity fkUsuarioEntity;

	@Column(name = "codigo_entrada", nullable = false, length = 15)
	private String codigoEntrada;

	@Column(name = "cantidad_inicial", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidadInicial;

	@Column(name = "cantidad_disponible", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidadDisponible;

	@Column(name = "precio_costo", nullable = false, precision = 12, scale = 4)
	private BigDecimal precioCosto;

	@Column(name = "fecha_ingreso", nullable = false, updatable = false)
	private OffsetDateTime fechaIngreso;

	@Column(name = "fecha_vencimiento")
	private LocalDate fechaVencimiento;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@OneToMany(mappedBy = "fkLoteEntity")
	private List<MovimientoInventarioEntity> movimientos = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.fechaIngreso = OffsetDateTime.now();
		this.creadoEn = OffsetDateTime.now();
	}
}
