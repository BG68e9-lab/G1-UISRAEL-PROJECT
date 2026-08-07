package com.uisrael.drinkhouse.presentacion.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultadoProductoDto {

private String nombre;

private String marca;

private String categoriaSugerida;

private String contenido;

private String informacionAdicional;

private Boolean reconocido;
}
