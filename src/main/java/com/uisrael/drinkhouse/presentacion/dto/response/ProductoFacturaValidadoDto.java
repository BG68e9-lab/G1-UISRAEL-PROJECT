package com.uisrael.drinkhouse.presentacion.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa un producto extraído de factura con información de validación.
 * Incluye nivel de confianza y coincidencias con productos existentes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoFacturaValidadoDto {

    /** Producto extraído de la factura */
    private ResultadoFacturaDto.ProductoFacturaDto producto;

    /** Nivel de confianza del OCR (0-100), basado en legibilidad */
    private Integer nivelConfianza;

    /** ID del producto coincidente en el sistema (null si no existe) */
    private Long productoIdCoincidente;

    /** Nombre del producto coincidente en el sistema */
    private String nombreProductoCoincidente;

    /** Porcentaje de similitud con el producto existente (0-100) */
    private Integer porcentajeSimilitud;

    /** Indica si se debe crear como producto nuevo */
    private Boolean requiereCreacion;

    /** Motivo por el cual requiere o no creación */
    private String motivoDecision;

    /** Indica si el producto es confiable para procesamiento automático (≥95% confianza) */
    public boolean esConfiable() {
        return nivelConfianza != null && nivelConfianza >= 95;
    }

    /** Indica si existe coincidencia exacta o muy alta (≥90% similitud) */
    public boolean tieneCoincidenciaAlta() {
        return porcentajeSimilitud != null && porcentajeSimilitud >= 90;
    }
}
