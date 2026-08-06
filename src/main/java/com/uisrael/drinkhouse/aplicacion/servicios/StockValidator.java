package com.uisrael.drinkhouse.aplicacion.servicios;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.aplicacion.excepciones.StockValidationException;

/**
 * Validador de cantidades de stock para movimientos de inventario.
 * 
 * Proporciona métodos para validar:
 * - La aritmética de stock (cantidad_anterior + ajuste = cantidad_posterior)
 * - La consistencia entre la cantidad anterior reclamada y el stock actual en la base de datos
 * 
 * Este componente es parte de la capa de aplicación y no tiene dependencias de infraestructura.
 */
@Component
public class StockValidator {

    /**
     * Valida que la aritmética de stock sea correcta.
     * Verifica que: cantidad_anterior + ajuste = cantidad_posterior
     * 
     * @param cantidadAnterior Cantidad de stock previa al movimiento
     * @param ajuste Ajuste a aplicar (positivo para entradas, negativo para salidas)
     * @param cantidadPosterior Cantidad de stock esperada después del movimiento
     * @throws StockValidationException si la validación aritmética falla
     */
    public void validateStockCalculation(
            BigDecimal cantidadAnterior, 
            BigDecimal ajuste, 
            BigDecimal cantidadPosterior) {
        
        if (cantidadAnterior == null || ajuste == null || cantidadPosterior == null) {
            throw new StockValidationException(
                "Validación de stock fallida: todos los valores (cantidad_anterior, ajuste, cantidad_posterior) son obligatorios"
            );
        }
        
        BigDecimal resultadoEsperado = cantidadAnterior.add(ajuste);
        
        if (resultadoEsperado.compareTo(cantidadPosterior) != 0) {
            throw new StockValidationException(
                String.format(
                    "Validación de stock fallida: cantidad_anterior (%s) + ajuste (%s) debe ser igual a cantidad_posterior (%s)",
                    cantidadAnterior.toPlainString(),
                    ajuste.toPlainString(),
                    cantidadPosterior.toPlainString()
                )
            );
        }
    }

    /**
     * Valida que la cantidad anterior reclamada coincida con el stock actual en la base de datos.
     * 
     * @param productoId Identificador del producto (usado para mensajes de error)
     * @param cantidadAnterior Cantidad anterior reclamada en la solicitud
     * @param currentStock Stock actual real desde la base de datos
     * @throws StockValidationException si la cantidad anterior no coincide con el stock actual
     */
    public void validateCurrentStock(
            Long productoId,
            BigDecimal cantidadAnterior,
            BigDecimal currentStock) {
        
        if (cantidadAnterior == null || currentStock == null) {
            throw new StockValidationException(
                String.format(
                    "Validación de stock fallida para producto %d: cantidad_anterior y stock actual son obligatorios",
                    productoId
                )
            );
        }
        
        if (cantidadAnterior.compareTo(currentStock) != 0) {
            throw new StockValidationException(
                String.format(
                    "Stock actual del producto %d no coincide: esperado %s, encontrado %s",
                    productoId,
                    currentStock.toPlainString(),
                    cantidadAnterior.toPlainString()
                )
            );
        }
    }

    /**
     * Valida que el stock resultante no sea negativo cuando el producto no permite stock negativo.
     * 
     * @param productoId Identificador del producto (usado para mensajes de error)
     * @param cantidadPosterior Cantidad de stock resultante después del movimiento
     * @param permiteStockNegativo Configuración del producto que indica si permite stock negativo
     * @throws StockValidationException si el stock sería negativo y el producto no lo permite
     */
    public void validateNegativeStock(
            Long productoId,
            BigDecimal cantidadPosterior,
            Boolean permiteStockNegativo) {
        
        if (cantidadPosterior == null) {
            throw new StockValidationException(
                String.format(
                    "Validación de stock fallida para producto %d: cantidad_posterior es obligatoria",
                    productoId
                )
            );
        }
        
        if (cantidadPosterior.compareTo(BigDecimal.ZERO) < 0) {
            if (permiteStockNegativo == null || !permiteStockNegativo) {
                throw new StockValidationException(
                    String.format(
                        "Stock negativo no permitido para producto %d: la cantidad resultante sería %s pero el producto no permite stock negativo",
                        productoId,
                        cantidadPosterior.toPlainString()
                    )
                );
            }
        }
    }
}
