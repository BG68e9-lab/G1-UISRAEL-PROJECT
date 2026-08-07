package com.uisrael.drinkhouse.aplicacion.excepciones;

public class StockValidationException extends ReglaNegocioException {

    public StockValidationException(String mensaje) {
        super(mensaje);
    }
}
