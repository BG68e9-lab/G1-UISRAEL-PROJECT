package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoIceRequestDto {

	@NotNull
	@DecimalMin(value = "0", message = "El valor del ICE no puede ser negativo")
	private BigDecimal valor;

	/**
	 * "PORCENTUAL" (ad-valorem, % sobre precioVenta) o "ESPECIFICO" (monto
	 * fijo). Si viene vacio se asume PORCENTUAL.
	 */
	private String tipoIce;

	private String motivo;
}
