package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Data;

/**
 * DTO de respuesta para notas de venta simplificadas.
 * Usado para visualización en el frontend.
 */
@Data
public class NotaVentaResponseDto {

	private Long notaId;
	private String fecha;
	private String nombreCliente;
	private String productoVendido;
	private String precioUnitario;
	private String total;
	private String observaciones;
	private OffsetDateTime creadoEn;
}
