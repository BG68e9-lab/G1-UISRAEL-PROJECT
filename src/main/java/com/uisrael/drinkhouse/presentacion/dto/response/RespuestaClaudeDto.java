package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaClaudeDto<T> {

private T resultado;

private Long tokensInput;

private Long tokensOutput;

private Long tokensCacheRead;

private Long tokensCacheWrite;
}
