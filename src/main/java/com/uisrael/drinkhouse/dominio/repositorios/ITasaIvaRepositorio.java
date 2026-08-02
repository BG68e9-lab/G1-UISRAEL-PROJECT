package com.uisrael.drinkhouse.dominio.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TasaIva;

public interface ITasaIvaRepositorio {

	/**
	 * Cierra la tasa vigente actual (si existe, poniendo vigenteHasta = ahora)
	 * y crea una nueva tasa vigente desde ahora. Devuelve la nueva tasa.
	 */
	TasaIva registrarNuevaTasa(BigDecimal porcentaje, String motivo);

	Optional<TasaIva> obtenerVigente();

	List<TasaIva> listarHistorial();
}
