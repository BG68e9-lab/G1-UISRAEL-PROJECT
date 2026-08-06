package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "historial_precios_producto")
public class HistorialPrecioProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "historial_precio_id")
	private Long historialPrecioId;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;
	
	@Column(name = "costo_promedio_anterior", precision = 12, scale = 4)
	private BigDecimal costoPromedioAnterior;
	
	@Column(name = "margen_ganancia_anterior", precision = 5, scale = 2)
	private BigDecimal margenGananciaAnterior;
	
	@Column(name = "precio_venta_anterior", precision = 12, scale = 2)
	private BigDecimal precioVentaAnterior;
	
	@Column(name = "costo_promedio_nuevo", nullable = false, precision = 12, scale = 4)
	private BigDecimal costoPromedioNuevo;
	
	@Column(name = "margen_ganancia_nuevo", precision = 5, scale = 2)
	private BigDecimal margenGananciaNuevo;
	
	@Column(name = "precio_venta_nuevo", nullable = false, precision = 12, scale = 2)
	private BigDecimal precioVentaNuevo;
	
	@Column(name = "motivo", length = 500)
	private String motivo;
	
	@Column(name = "usuario_modificador", nullable = false, length = 100)
	private String usuarioModificador;
	
	@Column(name = "fecha_cambio", nullable = false)
	private OffsetDateTime fechaCambio;
	
	@Column(name = "origen_cambio", nullable = false, length = 50)
	private String origenCambio;
	
	@Column(name = "factura_relacionada", length = 100)
	private String facturaRelacionada;
	
	@Column(name = "orden_compra_id")
	private Long ordenCompraId;
	
	@Column(name = "session_id", length = 100)
	private String sessionId;
	
	@Column(name = "direccion_ip", length = 45)
	private String direccionIp;
}
