package com.uisrael.drinkhouse.aplicacion.excepciones;

public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
