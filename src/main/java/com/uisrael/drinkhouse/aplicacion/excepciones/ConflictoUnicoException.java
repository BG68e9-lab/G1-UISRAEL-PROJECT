package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando se intenta crear un recurso que viola una
 * restricción de unicidad. Corresponde a respuestas HTTP 409 Conflict.
 */
public class ConflictoUnicoException extends RuntimeException {

	public ConflictoUnicoException(String mensaje) {
		super(mensaje);
	}
}
