package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import java.util.Optional;

public interface ICodigoAccesoUseCase {

	CodigoAcceso generarCodigo(String tipoCodigo, java.util.UUID usuarioId);

	CodigoAcceso validarCodigo(String codigoHash);

CodigoAcceso validarCodigoSinMarcar(String codigoHash);

CodigoAcceso marcarCodigoComoUsado(String codigoHash);
	
	Optional<CodigoAcceso> buscarUltimoCodigoPorUsuarioYTipo(java.util.UUID usuarioId, String tipoCodigo);
}
