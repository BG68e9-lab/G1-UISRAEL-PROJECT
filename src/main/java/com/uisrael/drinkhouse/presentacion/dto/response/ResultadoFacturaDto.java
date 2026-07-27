package com.uisrael.drinkhouse.presentacion.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que contiene el resultado estructurado de la extracción de datos
 * de una factura de compra a través de Claude Vision.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoFacturaDto {

    /** RUC del proveedor emisor de la factura, o null si no se pudo leer */
    private String rucProveedor;

    /** Razón social del proveedor emisor, o null si no se pudo leer */
    private String razonSocialProveedor;

    /** Fecha de la factura en formato YYYY-MM-DD, o null si no se pudo leer */
    private String fechaFactura;

    /** Número de factura, o null si no se pudo leer */
    private String numeroFactura;

    /** Lista de productos incluidos en la factura */
    private List<ProductoFacturaDto> productos;

    /** Total de la factura, o null si no se pudo leer */
    private Double totalFactura;

    /**
     * Representa un producto individual dentro de la factura.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductoFacturaDto {

        /** Nombre del producto */
        private String nombre;

        /** Marca del producto, o null */
        private String marca;

        /** Tipo de bebida, o null */
        private String tipo;

        /** Cantidad de unidades */
        private Integer cantidad;

        /** Precio unitario */
        private Double precioUnitario;

        /** Subtotal (cantidad × precio unitario) */
        private Double subtotal;
    }
}
