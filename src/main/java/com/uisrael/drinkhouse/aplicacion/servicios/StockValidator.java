package com.uisrael.drinkhouse.aplicacion.servicios;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.aplicacion.excepciones.StockValidationException;

@Component
public class StockValidator {

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
