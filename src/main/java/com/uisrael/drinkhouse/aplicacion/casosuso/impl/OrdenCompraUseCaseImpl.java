package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.repositorios.IOrdenCompraRepositorio;

public class OrdenCompraUseCaseImpl implements IOrdenCompraUseCase {

	private static final Map<String, Set<String>> TRANSICIONES_VALIDAS = Map.of(
			"BORRADOR", Set.of("ENVIADA", "ANULADA"),
			"ENVIADA", Set.of("RECIBIDA", "ANULADA"),
			"RECIBIDA", Set.of(),
			"ANULADA", Set.of());

	private final IOrdenCompraRepositorio repositorio;

	public OrdenCompraUseCaseImpl(IOrdenCompraRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public OrdenCompra crear(OrdenCompra ordenCompra) {
		ordenCompra.setOrdenCompraId(null);
		ordenCompra.setEstado("BORRADOR");
		return repositorio.guardar(ordenCompra);
	}

	@Override
	public OrdenCompra actualizar(Long id, OrdenCompra ordenCompra) {
		OrdenCompra existente = buscarPorId(id);
		if (!"BORRADOR".equals(existente.getEstado())) {
			throw new IllegalStateException("Solo se pueden editar ordenes en estado BORRADOR");
		}
		ordenCompra.setOrdenCompraId(id);
		ordenCompra.setEstado(existente.getEstado());
		return repositorio.guardar(ordenCompra);
	}

	@Override
	public OrdenCompra buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new NoSuchElementException("Orden de compra no encontrada: " + id));
	}

	@Override
	public OrdenCompra buscarPorCodigo(String codigoReferencia) {
		return repositorio.buscarPorCodigo(codigoReferencia)
				.orElseThrow(() -> new NoSuchElementException("Orden de compra no encontrada: " + codigoReferencia));
	}

	@Override
	public List<OrdenCompra> listar(String estado) {
		return repositorio.listarTodos(estado);
	}

	@Override
	public OrdenCompra cambiarEstado(Long id, String nuevoEstado) {
		OrdenCompra existente = buscarPorId(id);
		Set<String> transicionesPermitidas = TRANSICIONES_VALIDAS.getOrDefault(existente.getEstado(), Set.of());
		if (!transicionesPermitidas.contains(nuevoEstado)) {
			throw new IllegalStateException(
					"Transicion invalida: no se puede cambiar de " + existente.getEstado() + " a " + nuevoEstado);
		}
		return repositorio.cambiarEstado(id, nuevoEstado);
	}

	@Override
	public OrdenCompra recibir(Long id) {
		return repositorio.recibir(id);
	}

	@Override
	public void eliminar(Long id) {
		OrdenCompra existente = buscarPorId(id);
		if (!"BORRADOR".equals(existente.getEstado())) {
			throw new IllegalStateException("Solo se pueden eliminar ordenes en estado BORRADOR");
		}
		repositorio.eliminar(id);
	}
}
