package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class ProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_id")
	private Long productoId;

	@ManyToOne
	@JoinColumn(name = "negocio_id")
	private NegocioEntity fkNegocioEntity;

	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private CategoriaEntity fkCategoriaEntity;

	@Column(name = "nombre", nullable = false, length = 150)
	private String nombre;

	@Column(name = "marca", length = 100)
	private String marca;

	@Column(name = "tipo", length = 100)
	private String tipo;

	@Column(name = "descripcion", length = 500)
	private String descripcion;

	@Column(name = "costo_promedio", nullable = false, precision = 12, scale = 4)
	private BigDecimal costoPromedio;

	@Column(name = "margen_ganancia_pct", precision = 5, scale = 2)
	private BigDecimal margenGanancia;

	@Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVenta;

	@Column(name = "precio_personalizado", nullable = false)
	private Boolean precioPersonalizado;

	@Column(name = "stock_actual", nullable = false)
	private Integer stockActual;

	@Column(name = "stock_minimo", nullable = false)
	private Integer stockMinimo;

	@Column(name = "visible_sin_stock", nullable = false)
	private Boolean visibleSinStock;

	@Column(name = "origen_identificacion", length = 20)
	private String origenIdentificacion;

	@Column(name = "activo", nullable = false)
	private Boolean activo;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@Column(name = "actualizado_en", nullable = false)
	private OffsetDateTime actualizadoEn;

	@OneToMany(mappedBy = "fkProductoEntity")
	private List<LoteProductoEntity> lotes = new ArrayList<>();

	@OneToMany(mappedBy = "fkProductoEntity")
	private List<MovimientoInventarioEntity> movimientos = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
		this.actualizadoEn = OffsetDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		this.actualizadoEn = OffsetDateTime.now();
	}
}
