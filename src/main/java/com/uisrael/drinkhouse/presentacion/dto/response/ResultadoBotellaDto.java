package com.uisrael.drinkhouse.presentacion.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene el resultado estructurado de la identificación de una botella
 * de bebida alcohólica a través de Claude Vision.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoBotellaDto {

    /** Nombre del producto identificado */
    private String nombre;

    /** Marca de la bebida */
    private String marca;

    /**
     * Tipo de bebida: whisky, ron, vodka, gin, tequila, vino, cerveza, pisco u otro
     */
    private String tipo;

    /** Presentación del envase (750ml, 1L, 500ml, 375ml, otros) */
    private String presentacion;

    /** Graduación alcohólica en porcentaje, o null si no es visible */
    private String graduacionAlcohol;

    /** Indica si el producto fue reconocido con claridad */
    private Boolean reconocido;
}
