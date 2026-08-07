package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentificacionIaResponseDto {

private Long identificacionIaId;

private String nombreModelo;

private String resultado;

private String nombreSugerido;

private String marcaSugerida;

private String tipoSugerido;

private Boolean reconocido;

private String tipoIdentificacion;

private ResultadoProductoDto resultadoProducto;

private ResultadoBotellaDto resultadoBotella;

private ResultadoFacturaDto resultadoFactura;

private Long productoId;

private ValidacionProductoExternoDto validacionExterna;

private OffsetDateTime creadoEn;
}
