package com.uisrael.drinkhouse.presentacion.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoFacturaDto {

private String rucProveedor;

private String razonSocialProveedor;

private String fechaFactura;

private String numeroFactura;

private List<ProductoFacturaDto> productos;

private Double totalFactura;

@Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductoFacturaDto {

private String nombre;

private String marca;

private String tipo;

private Integer cantidad;

private Double precioUnitario;

private Double subtotal;

private Long productoId;

private boolean productoExiste;
    }
}
