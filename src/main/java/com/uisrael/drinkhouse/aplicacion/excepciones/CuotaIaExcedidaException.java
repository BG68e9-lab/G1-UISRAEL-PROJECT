package com.uisrael.drinkhouse.aplicacion.excepciones;

public class CuotaIaExcedidaException extends RuntimeException {

    public CuotaIaExcedidaException(String mensaje) {
        super(mensaje);
    }
}
