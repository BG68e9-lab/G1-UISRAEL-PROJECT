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
@Table(name = "producto_precio_historico")
public class ProductoPrecioHistoricoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "producto_id", nullable = false)
	private ProductoEntity fkProductoEntity;

	@Column(name = "costo_promedio", nullable = false, precision = 12, scale = 4)
	private BigDecimal costoPromedio;

	@Column(name = "margen_ganancia_pct", precision = 5, scale = 2)
	private BigDecimal margenGanancia;

	@Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVenta;

	@Column(name = "iva_porcentaje_aplicado", precision = 5, scale = 2)
	private BigDecimal ivaPorcentajeAplicado;

	@Column(name = "ice_tipo_aplicado", length = 20)
	private String iceTipoAplicado;

	@Column(name = "ice_valor_aplicado", precision = 12, scale = 4)
	private BigDecimal iceValorAplicado;

	@Column(name = "precio_final_con_impuestos", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioFinalConImpuestos;

	@Column(name = "vigente_desde", nullable = false)
	private OffsetDateTime vigenteDesde;

	@Column(name = "vigente_hasta")
	private OffsetDateTime vigenteHasta;

	@Column(name = "motivo", length = 255)
	private String motivo;

	@Column(name = "creado_en", nullable = false, updatable = false)
	private OffsetDateTime creadoEn;

	@PrePersist
	protected void onCreate() {
		this.creadoEn = OffsetDateTime.now();
	}
}
