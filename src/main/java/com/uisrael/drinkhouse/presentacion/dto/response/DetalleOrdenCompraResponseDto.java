package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class DetalleOrdenCompraResponseDto {

	private Long detalleOrdenCompraId;
	private Long ordenCompraId;
	private Long productoId;
	private String productoNombre;
	private String productoMarca;
	private String productoTipo;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal subtotal;
	private String observaciones;
}
