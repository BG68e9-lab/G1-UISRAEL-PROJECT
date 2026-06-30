package com.uisrael.drinkhouse.presentacion.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CodigoAccesoRequestDto {

    @NotNull
    private UUID usuarioId;

    @NotBlank
    private String tipoCodigo;

    @NotBlank
    private String codigoHash;

    @NotNull
    private OffsetDateTime expiraEn;
}