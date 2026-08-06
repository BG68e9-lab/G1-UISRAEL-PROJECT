package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "historial_iva_producto")
public class HistorialIvaProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "historial_iva_id")
	private Long historialIvaId;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;
	
	@Column(name = "tarifa_iva_anterior", precision = 5, scale = 2)
	private BigDecimal tarifaIvaAnterior;
	
	@Column(name = "codigo_porcentaje_anterior", length = 2)
	private String codigoPorcentajeAnterior;
	
	@Column(name = "descripcion_anterior", length = 100)
	private String descripcionAnterior;
	
	@Column(name = "tarifa_iva_nueva", nullable = false, precision = 5, scale = 2)
	private BigDecimal tarifaIvaNueva;
	
	@Column(name = "codigo_porcentaje_nuevo", nullable = false, length = 2)
	private String codigoPorcentajeNuevo;
	
	@Column(name = "descripcion_nueva", nullable = false, length = 100)
	private String descripcionNueva;
	
	@Column(name = "motivo", length = 500)
	private String motivo;
	
	@Column(name = "usuario_modificador", nullable = false, length = 100)
	private String usuarioModificador;
	
	@Column(name = "fecha_cambio", nullable = false)
	private OffsetDateTime fechaCambio;
	
	@Column(name = "origen_cambio", nullable = false, length = 50)
	private String origenCambio;
	
	@Column(name = "resolucion_sri", length = 100)
	private String resolucionSri;
	
	@Column(name = "fecha_vigencia")
	private LocalDate fechaVigencia;
	
	@Column(name = "session_id", length = 100)
	private String sessionId;
	
	@Column(name = "direccion_ip", length = 45)
	private String direccionIp;
}
