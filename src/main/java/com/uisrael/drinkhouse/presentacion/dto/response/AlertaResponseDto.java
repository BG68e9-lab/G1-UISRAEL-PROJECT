package com.uisrael.drinkhouse.presentacion.dto.response;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class AlertaResponseDto {

    private Long alertaId;
    private Integer negocioId;
    private String tipoAlerta;
    private String referenciaTipo;
    private Long referenciaId;
    private String mensaje;
    private Boolean atendida;
    private OffsetDateTime creadoEn;
}
