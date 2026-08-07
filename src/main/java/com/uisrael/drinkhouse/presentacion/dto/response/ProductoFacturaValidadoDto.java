package com.uisrael.drinkhouse.presentacion.dto.response;

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
public class ProductoFacturaValidadoDto {

private ResultadoFacturaDto.ProductoFacturaDto producto;

private Integer nivelConfianza;

private Long productoIdCoincidente;

private String nombreProductoCoincidente;

private Integer porcentajeSimilitud;

private Boolean requiereCreacion;

private String motivoDecision;

public boolean esConfiable() {
        return nivelConfianza != null && nivelConfianza >= 95;
    }

public boolean tieneCoincidenciaAlta() {
        return porcentajeSimilitud != null && porcentajeSimilitud >= 90;
    }
}
