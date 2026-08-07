package com.uisrael.drinkhouse.aplicacion.excepciones;

public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
