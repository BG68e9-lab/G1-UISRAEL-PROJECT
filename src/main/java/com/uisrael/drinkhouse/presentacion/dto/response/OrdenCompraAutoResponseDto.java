package com.uisrael.drinkhouse.presentacion.dto.response;

import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta para la creación automática de Orden de Compra desde factura IA.
 * Incluye información del proveedor (creado o existente) y la orden de compra generada.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraAutoResponseDto {

    /** Información de la identificación IA */
    private Long identificacionIaId;
    
    /** Datos del proveedor (creado o existente) */
    private ProveedorInfoDto proveedor;
    
    /** Indica si el proveedor fue creado automáticamente */
    private boolean proveedorNuevo;
    
    /** Orden de compra creada */
    private OrdenCompraResponseDto ordenCompra;
    
    /** Datos extraídos de la factura por IA */
    private ResultadoFacturaDto facturaExtraida;
    
    /** Mensaje informativo */
    private String mensaje;
    
    /**
     * Información básica del proveedor.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProveedorInfoDto {
        private Long proveedorId;
        private String ruc;
        private String razonSocial;
        private String email;
        private boolean emailTemporal;
    }
}
