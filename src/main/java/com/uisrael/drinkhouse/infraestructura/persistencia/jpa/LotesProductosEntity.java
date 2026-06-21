package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class LotesProductosEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lote_id")
	private Long loteId; 
	
	@Column(name = "negocio_id", nullable = false)
	private Integer negocioId;
	
	@Column(name = "producto_id", nullable = false)
	private Long productoId; 
	
	@Column(name = "orden_compra_id", nullable = false)
	private Long ordenCompraId; 
	
	@Column(name = "codigo_entrada", nullable = false, length = 50)
	private String codigoEntrada;
	
	@Column(name = "cantidad_inicial", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidadInicial; 
	
	@Column(name = "cantidad_disponible", nullable = false, precision = 10, scale = 2)
	private BigDecimal cantidadDisponible; 
	
	@Column(name = "precio_costo", precision = 10, scale = 2)
	private BigDecimal precioCosto;
	
	@Column(name = "fecha_ingreso")
	private Date fechaIngreso; 
	
	@Column(name = "fecha_vencimiento")
	private Date fechaVencimiento; 
	
	@Column(name = "estado_respaldo_id", nullable = false)
	private Integer estadoRespaldoId; 
	
	@Column(name = "registrado_por", nullable = false)
	private UUID registradoPor; 
}
