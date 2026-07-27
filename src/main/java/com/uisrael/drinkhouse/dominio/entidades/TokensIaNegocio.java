package com.uisrael.drinkhouse.dominio.entidades;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad de dominio que define el token de IA activo para un negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokensIaNegocio {

    private UUID tokenIaId;
    private Integer negocioId;
    private String tokenHash;
    private Boolean activo;
}
