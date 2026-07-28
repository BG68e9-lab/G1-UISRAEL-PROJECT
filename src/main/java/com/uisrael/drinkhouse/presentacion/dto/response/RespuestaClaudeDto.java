package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que envuelve la respuesta de Claude junto con los tokens consumidos.
 * @param <T> Tipo del resultado (ResultadoBotellaDto, ResultadoFacturaDto, etc.)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaClaudeDto<T> {
    
    /** Resultado estructurado de la identificación/extracción */
    private T resultado;
    
    /** Tokens de entrada consumidos (incluye imagen + prompt) */
    private Long tokensInput;
    
    /** Tokens de salida generados por Claude */
    private Long tokensOutput;
    
    /** Tokens cacheados (si se usó prompt caching) */
    private Long tokensCacheRead;
    
    /** Tokens de cache escritos */
    private Long tokensCacheWrite;
}
