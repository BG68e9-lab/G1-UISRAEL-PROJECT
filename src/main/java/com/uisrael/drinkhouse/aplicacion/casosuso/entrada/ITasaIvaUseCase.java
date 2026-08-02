package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TasaIva;

public interface ITasaIvaUseCase {

	TasaIva crearNuevaTasa(BigDecimal porcentaje, String motivo);

	Optional<TasaIva> obtenerVigente();

	List<TasaIva> listarHistorial();
}
