package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogAuditoriaRequestDto {

    @NotNull
    private Integer negocioId;

    @NotNull
    private UUID usuarioId;

    @NotBlank
    private String entidad;

    @NotBlank
    private String entidadId;

    @NotBlank
    private String accion;

    private String detalle;
}