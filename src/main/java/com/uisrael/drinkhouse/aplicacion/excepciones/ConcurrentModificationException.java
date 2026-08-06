package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando se detecta una modificación concurrente en el stock de un producto.
 * Se lanza después de agotar el número máximo de reintentos (3 intentos) debido a
 * conflictos de concurrencia con bloqueo optimista.
 * 
 * Corresponde a respuestas HTTP 409 Conflict.
 */
public class ConcurrentModificationException extends ReglaNegocioException {

    public ConcurrentModificationException(String mensaje) {
        super(mensaje);
    }
}
