package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO de respuesta uniforme para todos los errores de la API.
 * Estructura: {timestamp, status, error, message}
 */
@Getter
@Builder
public class ErrorResponseDto {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
}
