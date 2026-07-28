package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LoteProductoResponseDto {

	private Long loteId;
	private String codigoEntrada;
	private Long productoId;
	private String productoNombre;
	private String productoMarca;
	private String productoTipo;
	private Integer cantidadInicial;
	private Integer cantidadDisponible;
	private BigDecimal precioCosto;
	private LocalDate fechaIngreso;
	private LocalDate fechaVencimiento;
	private LocalDateTime fechaCreacion;
	private String usuarioCreacion;
	private Boolean activo;
	private Long ordenCompraId;
}
