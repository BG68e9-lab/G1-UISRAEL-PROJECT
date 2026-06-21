package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
public class ProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "producto_id")
	private Integer productoId;
	@Column(name = "negocio_id", nullable = false)
	private Integer negocioId;
	@Column(name = "categoria_id", nullable = false)
	private Integer categoriaId;
	@Column(name = "nombre", nullable = false, length = 50)
	private String nombre;
	@Column(name = "marca", nullable = false, length = 20)
	private String marca;
	@Column(name = "tipo", nullable = false, length = 20)
	private String tipo;
	@Column(name = "descripcion", nullable = false, length = 50)
	private String descripcion;
	@Column(name = "costo_promedio", nullable = false, precision = 5, scale = 2)
	private BigDecimal costoPromedio;
	@Column(name = "margen_ganancia_pct", nullable = false, precision = 5, scale = 2)
	private BigDecimal margenGanancia;
	@Column(name = "precio_venta", nullable = false, precision = 5, scale = 2)
	private BigDecimal precioVenta;
	@Column(name = "precio_personalizado", nullable = false, precision = 5, scale = 2)
	private BigDecimal precioPersonalizado;
	@Column(name = "stock_actual", nullable = false)
	private Integer stockActual;
	@Column(name = "stock_minimo", nullable = false)
	private Integer stockMinimo;
	@Column(name = "visible_sin_stock", nullable = false)
	private Boolean visibleSinStock;
	@Column(name = "orgen_identificacion", nullable = false, length = 20)
	private String origenIdentificacion;

}
