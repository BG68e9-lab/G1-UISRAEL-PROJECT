package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EstadoOcRequestDto {

    @NotBlank
    private String codigo;

    @NotBlank
    private String etiqueta;
}