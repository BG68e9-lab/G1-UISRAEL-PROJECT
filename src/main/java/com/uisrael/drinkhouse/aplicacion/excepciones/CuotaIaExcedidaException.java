package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando el negocio supera el límite mensual de tokens de IA.
 * Corresponde a respuestas HTTP 429 Too Many Requests.
 */
public class CuotaIaExcedidaException extends RuntimeException {

    public CuotaIaExcedidaException(String mensaje) {
        super(mensaje);
    }
}
