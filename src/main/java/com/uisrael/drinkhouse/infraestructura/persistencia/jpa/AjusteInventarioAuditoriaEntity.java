package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * JPA Entity for ajustes_inventario_auditoria table.
 * Stores complete audit trail for inventory movements with secondary authentication.
 * 
 * @see com.uisrael.drinkhouse.dominio.entidades.AjusteInventarioAuditoria
 */
@Data
@Entity
@Table(name = "ajustes_inventario_auditoria")
public class AjusteInventarioAuditoriaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ajuste_id")
	private Long ajusteId;

	@Column(name = "movimiento_id", nullable = false)
	private Long movimientoId;

	@Column(name = "producto_id", nullable = false)
	private Long productoId;

	@Column(name = "lote_id")
	private Long loteId;

	@Column(name = "tipo_movimiento", nullable = false, length = 30)
	private String tipoMovimiento;

	@Column(name = "cantidad_anterior", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidadAnterior;

	@Column(name = "ajuste", nullable = false, precision = 12, scale = 3)
	private BigDecimal ajuste;

	@Column(name = "cantidad_posterior", nullable = false, precision = 12, scale = 3)
	private BigDecimal cantidadPosterior;

	@Column(name = "usuario_autorizado", nullable = false, length = 100)
	private String usuarioAutorizado;

	@Column(name = "usuario_ejecutor", nullable = false, length = 100)
	private String usuarioEjecutor;

	@Column(name = "justificacion", nullable = false, length = 500)
	private String justificacion;

	@Column(name = "fecha_hora", nullable = false)
	private OffsetDateTime fechaHora;

	@Column(name = "direccion_ip", length = 45)
	private String direccionIp;

	@Column(name = "session_id", length = 100)
	private String sessionId;

	@Column(name = "venta_id")
	private Long ventaId;

	/**
	 * Sets fecha_hora to current timestamp before persisting.
	 * Ensures every audit record has an accurate creation timestamp.
	 */
	@PrePersist
	protected void onCreate() {
		this.fechaHora = OffsetDateTime.now();
	}
}
