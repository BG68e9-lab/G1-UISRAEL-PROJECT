package com.uisrael.drinkhouse.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrdenCompraResponseDto {

	private Long ordenCompraId;
	private String codigoReferencia;
	private Long proveedorId;
	private String proveedorRazonSocial;
	private Integer negocioId;
	private String estado;
	private BigDecimal total;
	private LocalDateTime fechaCreacion;
	private String usuarioCreacion;
	private String observaciones;
	private Long version;
	private List<DetalleOrdenCompraResponseDto> detalles;
}
