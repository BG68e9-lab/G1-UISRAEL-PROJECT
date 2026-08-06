package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para registrar un movimiento de inventario.
 * Cubre entradas, salidas y ajustes de stock.
 */
@Data
@Schema(description = "Datos de solicitud para crear un movimiento de inventario con auditoría y validación de stock")
public class MovimientoInventarioRequestDto {

	/** ID del producto afectado por el movimiento. */
	@Schema(description = "ID del producto afectado por el movimiento de inventario", example = "101", required = true)
	@NotNull(message = "El productoId es obligatorio")
	private Long productoId;

	/** ID del lote (requerido para movimientos de tipo SALIDA). */
	@Schema(description = "ID del lote del producto (opcional para ENTRADA/AJUSTE, requerido para SALIDA)", example = "50", nullable = true)
	private Long loteId;

	/** ID del tipo de movimiento (ENTRADA, SALIDA, AJUSTE). */
	@Schema(description = "ID del tipo de movimiento: 1=ENTRADA, 2=SALIDA, 3=AJUSTE_POSITIVO, 4=AJUSTE_NEGATIVO", example = "3", required = true)
	@NotNull(message = "El tipoMovimientoId es obligatorio")
	private Long tipoMovimientoId;

	/** 
	 * Cantidad del movimiento.
	 * Para ENTRADA y SALIDA debe ser positiva.
	 * Para AJUSTE puede ser positiva (aumentar) o negativa (disminuir).
	 */
	@Schema(description = "Cantidad del movimiento. Positiva para entradas/incrementos, negativa para salidas/decrementos", example = "50.00", required = true)
	@NotNull(message = "La cantidad es obligatoria")
	private BigDecimal cantidad;

	/** Precio unitario del producto en el movimiento (opcional). */
	@Schema(description = "Precio unitario del producto en el momento del movimiento", example = "2.50", nullable = true)
	private BigDecimal precioUnitario;

	/** 
	 * Cantidad de stock antes del movimiento.
	 * Debe coincidir con el stock actual del producto en la base de datos.
	 * OPCIONAL: Solo requerido para movimientos con auditoría.
	 */
	@Schema(description = "Cantidad de stock actual del producto antes del movimiento. Debe coincidir con el stock en base de datos (opcional para ventas simples)", example = "100.00", nullable = true)
	private BigDecimal cantidadAnterior;

	/** 
	 * Ajuste a aplicar al stock.
	 * Positivo para incrementos, negativo para decrementos.
	 * OPCIONAL: Solo requerido para movimientos con auditoría.
	 */
	@Schema(description = "Ajuste de stock a aplicar. Positivo para incrementos, negativo para decrementos (opcional para ventas simples)", example = "50.00", nullable = true)
	private BigDecimal ajuste;

	/** 
	 * Cantidad de stock esperada después del movimiento.
	 * Debe cumplir: cantidadAnterior + ajuste = cantidadPosterior
	 * OPCIONAL: Solo requerido para movimientos con auditoría.
	 */
	@Schema(description = "Cantidad de stock esperada después del movimiento. Debe cumplir: cantidadAnterior + ajuste = cantidadPosterior (opcional para ventas simples)", example = "150.00", nullable = true)
	private BigDecimal cantidadPosterior;

	/** 
	 * Justificación del movimiento de inventario.
	 * Texto explicativo requerido para auditoría (10-500 caracteres).
	 * OPCIONAL: Solo requerido para movimientos con auditoría.
	 */
	@Schema(description = "Justificación del movimiento para auditoría. Debe tener entre 10 y 500 caracteres (opcional para ventas simples)", 
		example = "Ajuste por inventario físico realizado el 15/01/2024", 
		nullable = true, 
		minLength = 10, 
		maxLength = 500)
	private String justificacion;

	/** 
	 * Usuario que autorizó el movimiento mediante autenticación secundaria.
	 * Obtenido del servicio de autenticación secundaria.
	 * OPCIONAL: Solo requerido para movimientos con auditoría.
	 */
	@Schema(description = "Usuario que autorizó el movimiento mediante código de autenticación secundaria (opcional para ventas simples)", example = "jperez", nullable = true)
	private String usuarioAutorizado;

	/** 
	 * Dirección IP desde donde se realizó la solicitud.
	 * Campo opcional para trazabilidad.
	 * Debe ser una dirección IPv4 o IPv6 válida.
	 */
	@Schema(description = "Dirección IP del cliente que realizó la solicitud (formato IPv4 o IPv6)", example = "192.168.1.100", nullable = true)
	@Pattern(
		regexp = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$|^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}::(?:[0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}$|^[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}::(?:[0-9a-fA-F]{1,4}:){0,4}[0-9a-fA-F]{1,4}$|^(?:[0-9a-fA-F]{1,4}:){0,2}[0-9a-fA-F]{1,4}::(?:[0-9a-fA-F]{1,4}:){0,3}[0-9a-fA-F]{1,4}$|^(?:[0-9a-fA-F]{1,4}:){0,3}[0-9a-fA-F]{1,4}::(?:[0-9a-fA-F]{1,4}:){0,2}[0-9a-fA-F]{1,4}$|^(?:[0-9a-fA-F]{1,4}:){0,4}[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}:[0-9a-fA-F]{1,4}$|^(?:[0-9a-fA-F]{1,4}:){0,5}[0-9a-fA-F]{1,4}::[0-9a-fA-F]{1,4}$|^(?:[0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4}::$",
		message = "La dirección IP debe ser un formato IPv4 o IPv6 válido"
	)
	private String direccionIp;

	/** 
	 * ID de sesión del usuario ejecutor.
	 * Campo opcional para trazabilidad.
	 */
	@Schema(description = "ID de sesión HTTP del usuario que ejecutó el movimiento", example = "AB123456789", nullable = true)
	private String sessionId;

	/** 
	 * ID de venta asociada al movimiento (opcional).
	 * Solo aplicable para movimientos vinculados a ventas.
	 */
	@Schema(description = "ID de venta asociada al movimiento (solo para movimientos vinculados a ventas)", example = "555", nullable = true)
	private Long ventaId;
}
