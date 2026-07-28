package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class MovimientoInventarioResponseDto {

	private Long movimientoId;
	private String codigoMovimiento;
	private String tipo;
	private Long productoId;
	private String productoNombre;
	private String productoMarca;
	private String productoTipo;
	private Long loteId;
	private String loteCodigoEntrada;
	private Integer cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal valorTotal;
	private String descripcion;
	private LocalDateTime fechaMovimiento;
	private String usuarioCreacion;
}
