package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NegocioRequestDto {

    @NotBlank
    private String nombre;

    @NotBlank
    private String ruc; 
}