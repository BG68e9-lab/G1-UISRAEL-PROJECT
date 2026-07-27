package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class NegocioRequestDto {

    @NotBlank
    private String nombre;

    @NotBlank
    @Pattern(regexp = "\\d{13}", message = "El RUC debe tener exactamente 13 dígitos numéricos")
    private String ruc;

    private Boolean activo;
}