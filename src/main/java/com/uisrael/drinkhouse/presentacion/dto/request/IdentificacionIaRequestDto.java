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

    /** 
     * ID del producto a identificar (OPCIONAL)
     * - null: Identificación de un producto NUEVO (caso más común)
     * - valor: Asociar identificación a un producto existente (para historial)
     */
    private Long productoId;

    /** ID del negocio que realiza la identificación */
    @NotNull(message = "El negocioId es obligatorio")
    private Integer negocioId;

    /**
     * Tipo de identificación a realizar:
     * - "PRODUCTO": Identificación genérica para cualquier producto (bebidas, snacks, alimentos, etc.)
     * - "BOTELLA": Identificación específica para bebidas (incluye graduación alcohólica)
     * - "FACTURA": Extracción de datos de facturas de compra
     */
    @NotBlank(message = "El tipoIdentificacion es obligatorio (PRODUCTO, BOTELLA o FACTURA)")
    private String tipoIdentificacion;
}
