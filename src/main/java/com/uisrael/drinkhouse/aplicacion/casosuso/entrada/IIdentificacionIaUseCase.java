package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;

public interface IIdentificacionIaUseCase {

IdentificacionIa identificarProducto(String imagenBase64, String formatoImagen,
                                         Long productoId, Integer negocioId, String tipoIdentificacion);

List<IdentificacionIa> consultarHistorial(Long productoId, OffsetDateTime desde, OffsetDateTime hasta);
}
