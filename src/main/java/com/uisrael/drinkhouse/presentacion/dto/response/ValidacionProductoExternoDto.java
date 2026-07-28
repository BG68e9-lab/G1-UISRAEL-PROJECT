package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa el resultado de una validación externa de producto
 * contra bases de datos de mercado (códigos de barras, APIs externas, etc.)
 * Simplificado para validar solo los campos esenciales de la entidad Producto.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionProductoExternoDto {

    /**
     * Indica si el producto fue encontrado y validado en bases de datos externas
     */
    private Boolean validado;

    /**
     * Nombre del producto según la validación externa
     */
    private String nombre;

    /**
     * Marca del producto según la validación externa
     */
    private String marca;

    /**
     * Tipo/categoría del producto según la validación externa
     * (ej: "bebidas", "snacks", "whisky", "ron", etc.)
     */
    private String tipo;

    /**
     * Descripción o información adicional del producto
     */
    private String descripcion;

    /**
     * Fuente de la validación (ej: "OpenFoodFacts", "UPCDatabase", "Manual")
     */
    private String fuente;

    /**
     * Mensaje adicional o advertencia de la validación
     */
    private String mensaje;
}
