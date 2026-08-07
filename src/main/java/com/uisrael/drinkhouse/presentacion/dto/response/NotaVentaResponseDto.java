package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Data;

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
