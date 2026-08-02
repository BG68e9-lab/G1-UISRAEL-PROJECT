package com.uisrael.drinkhouse.presentacion.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProductoRequestDto {

	@NotBlank
	@Pattern(regexp = ".*\\p{L}.*", message = "El nombre no puede ser solo numeros; debe incluir al menos una letra (ej: '220V' es valido, '11111' no)")
	private String nombre;

	private String marca;

	private String tipo;

	private String descripcion;

	@NotNull
	private BigDecimal costoPromedio;

	private BigDecimal margenGanancia;

	/**
	 * Opcional: si precioPersonalizado es false, se calcula automaticamente
	 * (costoPromedio + margen) en el caso de uso. Si precioPersonalizado es
	 * true, es obligatorio (validado en el caso de uso, no aqui).
	 */
	private BigDecimal precioVenta;

	@NotNull
	private Boolean precioPersonalizado;

	@NotNull
	private Integer stockActual;

	@NotNull
	private Integer stockMinimo;

	@NotNull
	private Boolean visibleSinStock;

	private String origenIdentificacion;

	/**
	 * Opcional (default false si no se envia): producto exento de IVA.
	 */
	private Boolean ivaExento;
}
