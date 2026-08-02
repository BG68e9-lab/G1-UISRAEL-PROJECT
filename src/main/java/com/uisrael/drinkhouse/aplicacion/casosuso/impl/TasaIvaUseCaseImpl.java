package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITasaIvaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.TasaIva;
import com.uisrael.drinkhouse.dominio.repositorios.ITasaIvaRepositorio;

public class TasaIvaUseCaseImpl implements ITasaIvaUseCase {

	private final ITasaIvaRepositorio repositorio;

	public TasaIvaUseCaseImpl(ITasaIvaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public TasaIva crearNuevaTasa(BigDecimal porcentaje, String motivo) {
		if (porcentaje == null || porcentaje.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("El porcentaje de IVA es obligatorio y no puede ser negativo");
		}
		return repositorio.registrarNuevaTasa(porcentaje, motivo);
	}

	@Override
	public Optional<TasaIva> obtenerVigente() {
		return repositorio.obtenerVigente();
	}

	@Override
	public List<TasaIva> listarHistorial() {
		return repositorio.listarHistorial();
	}
}
