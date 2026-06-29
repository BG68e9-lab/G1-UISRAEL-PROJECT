package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProveedorRequestDto {
	
	private Integer proveedorId;
	@NotBlank
	private Integer negocioId;
	@NotBlank
	private String ruc;
	@NotBlank
	private String razonSocial;
	@NotBlank
	private String direccion;
	@NotBlank
	private String telefono;
	@NotBlank
	private String email;

}
