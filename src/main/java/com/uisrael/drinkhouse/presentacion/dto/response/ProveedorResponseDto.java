package com.uisrael.drinkhouse.presentacion.dto.response;

import lombok.Data;

@Data
public class ProveedorResponseDto {

	private Long proveedorId;
	private String ruc;
	private String razonSocial;
	private String direccion;
	private String telefono;
	private String email;
}
