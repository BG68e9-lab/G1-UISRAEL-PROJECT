package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoIceUseCase;
import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoIceRepositorio;

public class ProductoIceUseCaseImpl implements IProductoIceUseCase {

	private final IProductoIceRepositorio repositorio;

	public ProductoIceUseCaseImpl(IProductoIceRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public ProductoIceHistorico crearNuevaTasa(Long productoId, BigDecimal valor, String tipoIce, String motivo) {
		if (productoId == null) {
			throw new IllegalArgumentException("El producto es obligatorio");
		}
		if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("El valor del ICE es obligatorio y no puede ser negativo");
		}
		String tipo = normalizarTipo(tipoIce);
		return repositorio.registrarNuevaTasa(productoId, valor, tipo, motivo);
	}

	private String normalizarTipo(String tipoIce) {
		if (tipoIce == null || tipoIce.isBlank()) {
			return ProductoIceHistorico.TIPO_PORCENTUAL;
		}
		String normalizado = tipoIce.trim().toUpperCase();
		if (!ProductoIceHistorico.TIPO_PORCENTUAL.equals(normalizado)
				&& !ProductoIceHistorico.TIPO_ESPECIFICO.equals(normalizado)) {
			throw new IllegalArgumentException(
					"tipoIce debe ser " + ProductoIceHistorico.TIPO_PORCENTUAL + " o " + ProductoIceHistorico.TIPO_ESPECIFICO);
		}
		return normalizado;
	}

	@Override
	public Optional<ProductoIceHistorico> obtenerVigente(Long productoId) {
		return repositorio.obtenerVigente(productoId);
	}

	@Override
	public List<ProductoIceHistorico> listarHistorial(Long productoId) {
		return repositorio.listarHistorial(productoId);
	}
}
