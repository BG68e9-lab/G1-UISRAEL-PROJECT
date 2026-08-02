package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoPrecioHistoricoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.ProductoPrecioHistorico;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoPrecioHistoricoRepositorio;

public class ProductoPrecioHistoricoUseCaseImpl implements IProductoPrecioHistoricoUseCase {

	private final IProductoPrecioHistoricoRepositorio repositorio;

	public ProductoPrecioHistoricoUseCaseImpl(IProductoPrecioHistoricoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public ProductoPrecioHistorico registrarSnapshot(Long productoId, BigDecimal costoPromedio,
			BigDecimal margenGanancia, BigDecimal precioVenta, BigDecimal ivaPorcentajeAplicado,
			String iceTipoAplicado, BigDecimal iceValorAplicado, String motivo) {
		return repositorio.registrarSnapshot(productoId, costoPromedio, margenGanancia, precioVenta,
				ivaPorcentajeAplicado, iceTipoAplicado, iceValorAplicado, motivo);
	}

	@Override
	public Optional<ProductoPrecioHistorico> obtenerVigente(Long productoId) {
		return repositorio.obtenerVigente(productoId);
	}

	@Override
	public List<ProductoPrecioHistorico> listarHistorial(Long productoId) {
		return repositorio.listarHistorial(productoId);
	}
}
