package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.uisrael.drinkhouse.aplicacion.excepciones.ConcurrentModificationException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.CuotaIaExcedidaException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ServicioNoDisponibleException;
import com.uisrael.drinkhouse.aplicacion.excepciones.StockValidationException;
import com.uisrael.drinkhouse.presentacion.dto.response.ErrorResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(404)
                .body(construirError(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(ConflictoUnicoException.class)
    public ResponseEntity<ErrorResponseDto> manejarConflicto(ConflictoUnicoException ex) {
        return ResponseEntity.status(409)
                .body(construirError(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDto> manejarRegla(ReglaNegocioException ex) {
        return ResponseEntity.status(422)
                .body(construirError(422, "Unprocessable Entity", ex.getMessage()));
    }

    @ExceptionHandler(CuotaIaExcedidaException.class)
    public ResponseEntity<ErrorResponseDto> manejarCuotaIa(CuotaIaExcedidaException ex) {
        return ResponseEntity.status(429)
                .body(construirError(429, "Too Many Requests", ex.getMessage()));
    }

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponseDto> manejarServicioNoDisponible(ServicioNoDisponibleException ex) {
        return ResponseEntity.status(503)
                .body(construirError(503, "Service Unavailable", ex.getMessage()));
    }

    @ExceptionHandler(StockValidationException.class)
    public ResponseEntity<ErrorResponseDto> manejarValidacionStock(StockValidationException ex) {
        logger.warn("Validación de stock fallida: {}", ex.getMessage());
        return ResponseEntity.status(400)
                .body(construirError(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponseDto> manejarModificacionConcurrente(ConcurrentModificationException ex) {
        logger.warn("Conflicto de concurrencia detectado: {}", ex.getMessage());
        return ResponseEntity.status(409)
                .body(construirError(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> manejarValidacion(MethodArgumentNotValidException ex) {
        String campo = ex.getBindingResult().getFieldErrors().get(0).getField();
        return ResponseEntity.status(400)
                .body(construirError(400, "Bad Request", "Campo inválido: " + campo));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> manejarJsonMalFormado(HttpMessageNotReadableException ex) {
        return ResponseEntity.status(400)
                .body(construirError(400, "Bad Request", "JSON mal formado"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> manejarTipoIncorrecto(MethodArgumentTypeMismatchException ex) {
        String parametro = ex.getName();
        String valorRecibido = String.valueOf(ex.getValue());
        String tipoEsperado = ex.getRequiredType().getSimpleName();
        
        String mensaje = String.format("Parámetro '%s' inválido. Se recibió '%s' pero se esperaba un %s", 
                                     parametro, valorRecibido, tipoEsperado);
        
        return ResponseEntity.status(400)
                .body(construirError(400, "Bad Request", mensaje));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> manejarMetodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        String mensaje = String.format("Método '%s' no permitido para esta ruta", ex.getMethod());
        return ResponseEntity.status(405)
                .body(construirError(405, "Method Not Allowed", mensaje));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> manejarGeneral(Exception ex) {
        logger.error("Error interno no manejado: {}", ex.getMessage(), ex);
        
        String sanitizedMessage = sanitizeErrorMessage(ex.getMessage());
        
        return ResponseEntity.status(500)
                .body(construirError(500, "Internal Server Error",
                        "Error interno: " + sanitizedMessage));
    }

    private ErrorResponseDto construirError(int status, String error, String message) {
        return ErrorResponseDto.builder()
                .timestamp(OffsetDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .build();
    }

private String sanitizeErrorMessage(String message) {
        if (message == null) {
            return "Error procesando la solicitud";
        }
        
        String sanitized = message
            .replaceAll("(?i)constraint \\[.*?\\]", "constraint violation")
            .replaceAll("(?i)table \".*?\"", "table")
            .replaceAll("(?i)column \".*?\"", "column")
            .replaceAll("(?i)relation \".*?\"", "relation")
            .replaceAll("(?i)key \\(.*?\\)=\\(.*?\\)", "key constraint")
            .replaceAll("(?i)detail:.*", "")
            .replaceAll("(?i)hint:.*", "");
        
        if (message.matches(".*\\b(SQL|ERROR|psql).*")) {
            return "Error de base de datos. Contacte al administrador";
        }
        
        return sanitized.trim();
    }
}
