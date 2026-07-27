package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando un servicio del sistema no está disponible o falla
 * de forma irrecuperable (por ejemplo, agotamiento de reintentos en secuencias).
 * Corresponde a respuestas HTTP 503 Service Unavailable.
 */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
