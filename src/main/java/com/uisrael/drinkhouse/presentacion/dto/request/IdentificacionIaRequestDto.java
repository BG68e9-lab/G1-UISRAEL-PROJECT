package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de solicitud para la identificación de un producto mediante IA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentificacionIaRequestDto {

    /** Imagen del producto codificada en Base64 */
    @NotBlank(message = "La imagen en base64 es obligatoria")
    private String imagenBase64;

    /** Formato de la imagen: JPEG, PNG o WEBP */
    private String formatoImagen;

    /** ID del producto a identificar */
    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    /** ID del negocio que realiza la identificación */
    @NotNull(message = "El negocioId es obligatorio")
    private Integer negocioId;

    /**
     * Tipo de identificación a realizar: "BOTELLA" para identificar una botella
     * de bebida alcohólica, o "FACTURA" para extraer datos de una factura de compra.
     */
    @NotBlank(message = "El tipoIdentificacion es obligatorio (BOTELLA o FACTURA)")
    private String tipoIdentificacion;
}
