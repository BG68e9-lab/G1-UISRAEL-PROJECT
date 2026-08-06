package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import java.util.Optional;

public interface ICodigoAccesoUseCase {

	CodigoAcceso generarCodigo(String tipoCodigo, java.util.UUID usuarioId);

	CodigoAcceso validarCodigo(String codigoHash);
	
	/**
	 * Valida un código sin marcarlo como usado.
	 * Útil para verificación de acceso sin consumir el código.
	 */
	CodigoAcceso validarCodigoSinMarcar(String codigoHash);
	
	/**
	 * Marca un código como usado.
	 * @param codigoHash El código a marcar como usado
	 * @return El código actualizado
	 */
	CodigoAcceso marcarCodigoComoUsado(String codigoHash);
	
	Optional<CodigoAcceso> buscarUltimoCodigoPorUsuarioYTipo(java.util.UUID usuarioId, String tipoCodigo);
}
