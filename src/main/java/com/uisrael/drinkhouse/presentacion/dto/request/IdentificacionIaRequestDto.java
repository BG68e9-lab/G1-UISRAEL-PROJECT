package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentificacionIaRequestDto {

@NotBlank(message = "La imagen en base64 es obligatoria")
    private String imagenBase64;

private String formatoImagen;

private Long productoId;

@NotNull(message = "El negocioId es obligatorio")
    private Integer negocioId;

@NotBlank(message = "El tipoIdentificacion es obligatorio (PRODUCTO, BOTELLA o FACTURA)")
    private String tipoIdentificacion;
}
