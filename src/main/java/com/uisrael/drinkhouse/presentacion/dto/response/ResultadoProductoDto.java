package com.uisrael.drinkhouse.presentacion.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO genérico para identificación de cualquier producto (bebidas, snacks, alimentos, etc.).
 * Campos adaptables según el tipo de producto detectado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoProductoDto {

    /** Nombre del producto identificado */
    private String nombre;

    /** Marca del producto */
    private String marca;

    /**
     * Categoría sugerida del producto:
     * - Para bebidas: whisky, ron, vodka, gin, tequila, vino, cerveza, pisco, gaseosa, agua, jugo, etc.
     * - Para snacks: papas, galletas, chocolate, caramelos, chicles, etc.
     * - Para alimentos: pan, arroz, fideos, conservas, etc.
     * - Otros: productos de limpieza, higiene, etc.
     */
    private String categoriaSugerida;

    /** 
     * Contenido/Presentación del producto (750ml, 1L, 500g, 250g, etc.)
     * null si no es visible
     */
    private String contenido;

    /** 
     * Información adicional específica del producto:
     * - Para bebidas alcohólicas: graduación alcohólica (ej: "40%")
     * - Para alimentos: información nutricional relevante
     * null si no aplica o no es visible
     */
    private String informacionAdicional;

    /** Indica si el producto fue reconocido con claridad */
    private Boolean reconocido;
}
