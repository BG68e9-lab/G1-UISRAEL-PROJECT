package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INotaVentaUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;
import com.uisrael.drinkhouse.dominio.repositorios.INotaVentaRepositorio;

/**
 * Implementación del caso de uso para consultas de notas de venta.
 */
public class NotaVentaUseCaseImpl implements INotaVentaUseCase {

	private final INotaVentaRepositorio repositorio;

	public NotaVentaUseCaseImpl(INotaVentaRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public List<NotaVenta> listarTodas() {
		return repositorio.listarTodas();
	}

	@Override
	public NotaVenta buscarPorId(Long notaId) {
		return repositorio.buscarPorId(notaId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Nota de venta no encontrada con ID: " + notaId));
	}
}
