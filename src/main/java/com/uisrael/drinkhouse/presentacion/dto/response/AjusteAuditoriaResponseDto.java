package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Registro de auditoría completo de un movimiento de inventario con trazabilidad de autorización y cambios de stock")
public class AjusteAuditoriaResponseDto {

	@Schema(description = "ID único del registro de auditoría", example = "789")
	private Long ajusteId;
	
	@Schema(description = "ID del movimiento de inventario auditado", example = "1245")
	private Long movimientoId;
	
	@Schema(description = "ID del producto afectado", example = "101")
	private Long productoId;
	
	@Schema(description = "Nombre del producto auditado", example = "Coca Cola 2L")
	private String productoNombre;
	
	@Schema(description = "ID del lote del producto (si aplica)", example = "50", nullable = true)
	private Long loteId;
	
	@Schema(description = "Código del lote del producto (si aplica)", example = "LOTE-2024-01", nullable = true)
	private String loteCodigo;
	
	@Schema(description = "Tipo de movimiento realizado", example = "AJUSTE_POSITIVO")
	private String tipoMovimiento;
	
	@Schema(description = "Cantidad de stock antes del movimiento", example = "100.00")
	private BigDecimal cantidadAnterior;
	
	@Schema(description = "Ajuste aplicado al stock (positivo o negativo)", example = "50.00")
	private BigDecimal ajuste;
	
	@Schema(description = "Cantidad de stock después del movimiento", example = "150.00")
	private BigDecimal cantidadPosterior;
	
	@Schema(description = "Usuario que autorizó el movimiento mediante autenticación secundaria", example = "jperez")
	private String usuarioAutorizado;
	
	@Schema(description = "Usuario que ejecutó el movimiento (sesión principal)", example = "mjohnson")
	private String usuarioEjecutor;
	
	@Schema(description = "Justificación del movimiento para auditoría", example = "Ajuste por inventario físico realizado el 15/01/2024")
	private String justificacion;
	
	@Schema(description = "Fecha y hora exacta del movimiento (ISO 8601 con zona horaria)", example = "2024-01-15T14:30:00-05:00")
	private OffsetDateTime fechaHora;
	
	@Schema(description = "Dirección IP desde donde se realizó el movimiento", example = "192.168.1.100", nullable = true)
	private String direccionIp;
	
	@Schema(description = "ID de sesión HTTP del usuario ejecutor", example = "AB123456789", nullable = true)
	private String sessionId;
	
	@Schema(description = "ID de venta asociada al movimiento (si aplica)", example = "555", nullable = true)
	private Long ventaId;
}
