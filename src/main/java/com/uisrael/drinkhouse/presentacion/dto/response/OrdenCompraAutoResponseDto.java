package com.uisrael.drinkhouse.presentacion.dto.response;

import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenCompraAutoResponseDto {

private Long identificacionIaId;

private ProveedorInfoDto proveedor;

private boolean proveedorNuevo;

private OrdenCompraResponseDto ordenCompra;

private ResultadoFacturaDto facturaExtraida;

private String mensaje;

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
