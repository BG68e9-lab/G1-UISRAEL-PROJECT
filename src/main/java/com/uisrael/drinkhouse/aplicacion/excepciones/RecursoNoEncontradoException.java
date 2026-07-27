package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * Corresponde a respuestas HTTP 404 Not Found.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
