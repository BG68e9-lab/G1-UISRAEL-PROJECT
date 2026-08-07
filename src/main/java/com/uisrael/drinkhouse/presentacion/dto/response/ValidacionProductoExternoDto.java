package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionProductoExternoDto {

private Boolean validado;

private String nombre;

private String marca;

private String tipo;

private String descripcion;

private String fuente;

private String mensaje;
}
