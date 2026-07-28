package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para Secuencia de Código.
 * Incluye información del negocio y tipo de movimiento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecuenciaCodigoResponseDto {

	private Integer negocioId;
	private String negocioNombre;
	private Integer tipoMovimientoId;
	private String tipoMovimientoNombre;
	private String tipoMovimientoCodigo;
	private Long ultimoNumero;
}
