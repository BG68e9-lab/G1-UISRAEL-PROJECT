package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentificacionIa {

    private Long identificacionId;
    private Integer negocioId;
    private Long productoId;
    private String archivoUrl;
    private String modeloIaUsado;
    private String nombreSugerido;
    private String marcaSugerida;
    private String tipoSugerido;
    private Boolean reconocido;
    private Long ordenCompraRelacionadaId;
    private UUID confirmadoPor;
    private OffsetDateTime creadoEn;
}
