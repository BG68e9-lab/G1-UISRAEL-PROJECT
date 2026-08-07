package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Respuesta de un movimiento de inventario registrado exitosamente")
public class MovimientoInventarioResponseDto {

	@Schema(description = "ID único del movimiento de inventario", example = "1245")
	private Long movimientoId;
	
	@Schema(description = "Código de referencia del movimiento generado automáticamente", example = "MOV-2024-001245")
	private String codigoMovimiento;
	
	@Schema(description = "ID del producto afectado", example = "101")
	private Long productoId;
	
	@Schema(description = "ID del lote del producto (si aplica)", example = "50", nullable = true)
	private Long loteId;
	
	@Schema(description = "ID del tipo de movimiento", example = "3")
	private Long tipoMovimientoId;
	
	@Schema(description = "Descripción del tipo de movimiento", example = "AJUSTE_POSITIVO")
	private String tipoMovimiento;
	
	@Schema(description = "Cantidad del movimiento", example = "50.00")
	private BigDecimal cantidad;
	
	@Schema(description = "Precio unitario del producto en el movimiento", example = "2.50", nullable = true)
	private BigDecimal precioUnitario;
	
	@Schema(description = "Fecha y hora de creación del movimiento (ISO 8601 con zona horaria)", example = "2024-01-15T14:30:00-05:00")
	private OffsetDateTime creadoEn;
}
