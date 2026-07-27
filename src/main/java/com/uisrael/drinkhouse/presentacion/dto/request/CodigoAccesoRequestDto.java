package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CodigoAccesoRequestDto {

	@NotBlank
	private String tipoCodigo;

	@NotNull
	private UUID usuarioId;
}
