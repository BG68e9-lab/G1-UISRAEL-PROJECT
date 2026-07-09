package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ProductoResponseDto {

	private Long productoId;
	private String nombre;
	private String marca;
	private String tipo;
	private String descripcion;
	private BigDecimal costoPromedio;
	private BigDecimal margenGanancia;
	private BigDecimal precioVenta;
	private Boolean precioPersonalizado;
	private Integer stockActual;
	private Integer stockMinimo;
	private Boolean visibleSinStock;
	private String origenIdentificacion;
	private Boolean activo;
	private OffsetDateTime creadoEn;
	private OffsetDateTime actualizadoEn;
}
