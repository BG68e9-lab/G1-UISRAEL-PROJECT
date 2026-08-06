package com.uisrael.drinkhouse.aplicacion.excepciones;

/**
 * Excepción lanzada cuando falla la validación de cantidades de stock en movimientos de inventario.
 * Se utiliza cuando:
 * - La aritmética de stock no es válida (cantidad_anterior + ajuste ≠ cantidad_posterior)
 * - La cantidad anterior reclamada no coincide con el stock actual en la base de datos
 * 
 * Corresponde a respuestas HTTP 400 Bad Request.
 */
public class StockValidationException extends ReglaNegocioException {

    public StockValidationException(String mensaje) {
        super(mensaje);
    }
}
