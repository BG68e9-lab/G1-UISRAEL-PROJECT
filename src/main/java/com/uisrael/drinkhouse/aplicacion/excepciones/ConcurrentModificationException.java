package com.uisrael.drinkhouse.aplicacion.excepciones;

public class ConcurrentModificationException extends ReglaNegocioException {

    public ConcurrentModificationException(String mensaje) {
        super(mensaje);
    }
}
