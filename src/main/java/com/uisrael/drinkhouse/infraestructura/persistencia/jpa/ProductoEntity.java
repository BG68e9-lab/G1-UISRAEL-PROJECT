package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Data
@Entity
@Table(name = "productos")
public class ProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_id")
	private Long productoId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "negocio_id")
	@JsonIgnore
	private NegocioEntity fkNegocioEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoria_id")
	@JsonIgnore
	private CategoriaEntity fkCategoriaEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tipo_producto_id")
	@JsonIgnore
	private TipoProductoEntity fkTipoProductoEntity;

	@Column(name = "nombre", nullable = false, length = 150)
	private String nombre;

	@Column(name = "marca", length = 100)
	private String marca;

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

	@Column(name = "permite_stock_negativo", nullable = true)
	private Boolean permiteStockNegativo;

	@Column(name = "origen_identificacion", length = 20)
	private String origenIdentificacion;

	@Column(name = "activo", nullable = false)
	private Boolean activo;

	@Column(name = "tarifa_iva_actual", precision = 5, scale = 2)
	private BigDecimal tarifaIvaActual;

	@Column(name = "codigo_porcentaje_iva", length = 2)
	private String codigoPorcentajeIva;

	@Column(name = "aplica_ice")
	private Boolean aplicaIce;

	@Column(name = "tarifa_ice_porcentual", precision = 5, scale = 2)
	private BigDecimal tarifaIcePorcentual;

	@Column(name = "tarifa_ice_especifica", precision = 12, scale = 4)
	private BigDecimal tarifaIceEspecifica;

	@Column(name = "grupo_ice", length = 100)
	private String grupoIce;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@Column(name = "actualizado_en", nullable = false)
	private OffsetDateTime actualizadoEn;

	@Version
	@Column(name = "version")
	private Long version;

	@OneToMany(mappedBy = "fkProductoEntity", fetch = FetchType.LAZY)
	@JsonIgnore
	private List<LoteProductoEntity> lotes = new ArrayList<>();

	@OneToMany(mappedBy = "fkProductoEntity", fetch = FetchType.LAZY)
	@JsonIgnore
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
