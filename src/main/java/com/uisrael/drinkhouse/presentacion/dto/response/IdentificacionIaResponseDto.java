package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para una identificación de producto mediante IA.
 * Incluye los campos sugeridos por Claude y el resultado estructurado
 * según el tipo de identificación (PRODUCTO, BOTELLA o FACTURA).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentificacionIaResponseDto {

    /** Identificador único de la identificación */
    private Long identificacionIaId;

    /** Nombre del modelo de IA utilizado */
    private String nombreModelo;

    /** Resultado textual de la identificación */
    private String resultado;

    /** Nombre del producto sugerido por la IA */
    private String nombreSugerido;

    /** Marca del producto sugerida por la IA */
    private String marcaSugerida;

    /** Tipo de bebida sugerido por la IA */
    private String tipoSugerido;

    /** Indica si el producto fue reconocido por la IA */
    private Boolean reconocido;

    /** Tipo de identificación realizada: PRODUCTO, BOTELLA o FACTURA */
    private String tipoIdentificacion;

    /** Resultado estructurado cuando el tipo es PRODUCTO (genérico) */
    private ResultadoProductoDto resultadoProducto;

    /** Resultado estructurado cuando el tipo es BOTELLA */
    private ResultadoBotellaDto resultadoBotella;

    /** Resultado estructurado cuando el tipo es FACTURA */
    private ResultadoFacturaDto resultadoFactura;

    /** ID del producto identificado */
    private Long productoId;

    /** Resultado de la validación externa contra bases de datos de mercado */
    private ValidacionProductoExternoDto validacionExterna;

    /** Fecha y hora de creación */
    private OffsetDateTime creadoEn;
}
