package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando una operación viola una regla de negocio del dominio.
 * Corresponde a respuestas HTTP 422 Unprocessable Entity.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
