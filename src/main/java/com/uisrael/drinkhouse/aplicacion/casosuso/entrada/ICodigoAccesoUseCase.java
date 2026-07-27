package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;

public interface ICodigoAccesoUseCase {

	CodigoAcceso generarCodigo(String tipoCodigo, java.util.UUID usuarioId);

	CodigoAcceso validarCodigo(String codigoHash);
}
