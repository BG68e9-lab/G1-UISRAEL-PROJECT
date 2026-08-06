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
@Table(name = "historial_ice_producto")
public class HistorialIceProductoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "historial_ice_id")
	private Long historialIceId;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;
	
	@Column(name = "aplica_ice_anterior")
	private Boolean aplicaIceAnterior;
	
	@Column(name = "tarifa_ice_anterior", precision = 5, scale = 2)
	private BigDecimal tarifaIceAnterior;
	
	@Column(name = "valor_especifico_anterior", precision = 12, scale = 4)
	private BigDecimal valorEspecificoAnterior;
	
	@Column(name = "tipo_tarifa_anterior", length = 20)
	private String tipoTarifaAnterior;
	
	@Column(name = "aplica_ice_nuevo", nullable = false)
	private Boolean aplicaIceNuevo;
	
	@Column(name = "tarifa_ice_nueva", precision = 5, scale = 2)
	private BigDecimal tarifaIceNueva;
	
	@Column(name = "valor_especifico_nuevo", precision = 12, scale = 4)
	private BigDecimal valorEspecificoNuevo;
	
	@Column(name = "tipo_tarifa_nuevo", length = 20)
	private String tipoTarifaNuevo;
	
	@Column(name = "grupo_ice", length = 100)
	private String grupoIce;
	
	@Column(name = "es_monofasico", nullable = false)
	private Boolean esMonofasico;
	
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
