package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.NoSuchElementException;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;

public class MovimientoInventarioUseCaseImpl implements IMovimientoInventarioUseCase {

	private final IMovimientoInventarioRepositorio repositorio;

	public MovimientoInventarioUseCaseImpl(IMovimientoInventarioRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public MovimientoInventario guardar(MovimientoInventario movimientoInventario) {
		validarCantidad(movimientoInventario.getTipo(), movimientoInventario.getCantidad());
		return repositorio.guardar(movimientoInventario);
	}

	@Override
	public MovimientoInventario actualizar(Long id, MovimientoInventario movimientoInventario) {
		validarCantidad(movimientoInventario.getTipo(), movimientoInventario.getCantidad());
		return repositorio.actualizar(id, movimientoInventario);
	}

	/**
	 * ENTRADA/SALIDA representan cantidades fisicas que entran o salen, siempre
	 * positivas. AJUSTE puede ser positivo (se encontro mas stock) o negativo
	 * (merma, dano, vencimiento), pero nunca cero.
	 */
	private void validarCantidad(String tipo, Integer cantidad) {
		if (cantidad == null) {
			throw new IllegalArgumentException("La cantidad es obligatoria");
		}
		if (cantidad == 0) {
			throw new IllegalArgumentException("La cantidad no puede ser cero");
		}
		boolean requierePositiva = "ENTRADA".equalsIgnoreCase(tipo) || "SALIDA".equalsIgnoreCase(tipo);
		if (requierePositiva && cantidad <= 0) {
			throw new IllegalArgumentException("La cantidad debe ser mayor a cero para movimientos de tipo " + tipo);
		}
	}

	@Override
	public MovimientoInventario buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NoSuchElementException("Movimiento de inventario no encontrado: " + id));
	}

	@Override
	public List<MovimientoInventario> listarTodo(String tipo) {
		return repositorio.listarTodo(tipo);
	}

	@Override
	public void eliminar(Long id) {
		repositorio.eliminar(id);
	}
}
