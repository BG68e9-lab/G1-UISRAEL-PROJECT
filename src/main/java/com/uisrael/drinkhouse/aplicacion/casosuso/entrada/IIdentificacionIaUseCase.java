package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;

/**
 * Puerto de entrada para el módulo de Identificación IA.
 * Cubre la identificación de productos y la consulta del historial.
 */
public interface IIdentificacionIaUseCase {

    /**
     * Identifica un producto a partir de una imagen codificada en base64.
     * Valida el formato de imagen, verifica la existencia del producto,
     * comprueba la cuota mensual del negocio e incrementa el consumo de tokens.
     *
     * @param imagenBase64       imagen codificada en base64
     * @param formatoImagen      formato de la imagen (JPEG, PNG, WEBP)
     * @param productoId         ID del producto a identificar
     * @param negocioId          ID del negocio que realiza la identificación
     * @param tipoIdentificacion tipo de análisis: "BOTELLA" o "FACTURA"
     * @return resultado de la identificación persistido
     */
    IdentificacionIa identificarProducto(String imagenBase64, String formatoImagen,
                                         Long productoId, Integer negocioId, String tipoIdentificacion);

    /**
     * Consulta el historial de identificaciones IA con filtros opcionales.
     *
     * @param productoId ID del producto (puede ser null)
     * @param desde      fecha mínima de creación (puede ser null)
     * @param hasta      fecha máxima de creación (puede ser null)
     * @return lista de identificaciones ordenadas por creadoEn descendente
     */
    List<IdentificacionIa> consultarHistorial(Long productoId, OffsetDateTime desde, OffsetDateTime hasta);
}
